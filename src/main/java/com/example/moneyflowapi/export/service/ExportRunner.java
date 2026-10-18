package com.example.moneyflowapi.export.service;

import com.example.moneyflowapi.export.ExportFileNames;
import com.example.moneyflowapi.export.config.ExportProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportRunner {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String HEADER = "date,time_zone,category_name,type,amount,description,raw_date\n";

    private static final int MAX_CELL_LENGTH = 1000;
    private static final String FORMULA_PREFIXES = "=+-@";

    private static final String COUNT_SQL =
            "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND date >= ? AND date < ?";

    private static final String SELECT_SQL =
            "SELECT t.date, c.name, t.type, t.amount, t.description "
                    + "FROM transactions t JOIN categories c ON c.id = t.category_id "
                    + "WHERE t.user_id = ? AND t.date >= ? AND t.date < ? "
                    + "ORDER BY t.date DESC, t.id";

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;
    private final ExportJobService jobService;
    private final ExportProgressRegistry registry;
    private final ExportProperties properties;

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(properties.path());
    }

    @Async("exportExecutor")
    public void run(UUID jobId, UUID userId, Instant from, Instant to, ZoneId zone, String downloadName) {
        Path part = properties.path().resolve(jobId + ".csv.zip.part");
        Path target = properties.path().resolve(jobId + ".csv.zip");

        Timestamp tsFrom = Timestamp.from(from);
        Timestamp tsTo = Timestamp.from(to);

        try {
            Long total = jdbcTemplate.queryForObject(COUNT_SQL, Long.class, userId, tsFrom, tsTo);
            ExportProgressRegistry.Progress progress = registry.start(jobId, total == null ? 0 : total);
            jobService.markRunning(jobId);

            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            tx.setReadOnly(true);

            try (ZipOutputStream zip = new ZipOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(part), 65536));
                 Writer writer = new BufferedWriter(
                         new OutputStreamWriter(zip, StandardCharsets.UTF_8), 65536)) {

                zip.putNextEntry(new ZipEntry(ExportFileNames.csvName(downloadName)));
                writer.write('\uFEFF');
                writer.write(HEADER);

                tx.executeWithoutResult(status -> {
                    jdbcTemplate.execute("SET LOCAL statement_timeout = '120s'");
                    jdbcTemplate.query(con -> {
                        PreparedStatement ps = con.prepareStatement(SELECT_SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                        ps.setFetchSize(2000);
                        ps.setObject(1, userId);
                        ps.setTimestamp(2, tsFrom);
                        ps.setTimestamp(3, tsTo);
                        return ps;
                    }, (RowCallbackHandler) rs -> {
                        try {
                            Instant at = rs.getTimestamp(1).toInstant();
                            BigDecimal amount = rs.getBigDecimal(4);
                            writer.write(DATE_FORMAT.format(at.atZone(zone)) + ","
                                    + zone.getId() + ","
                                    + csv(rs.getString(2)) + ","
                                    + csv(rs.getString(3)) + ","
                                    + (amount == null ? "" : amount.toPlainString()) + ","
                                    + csv(rs.getString(5)) + ","
                                    + at + "\n");
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }

                        progress.processed().incrementAndGet();
                    });
                });
            }

            Files.move(part, target, StandardCopyOption.ATOMIC_MOVE);
            jobService.markReady(jobId, target.getFileName().toString(), Files.size(target));
        } catch (Exception e) {
            log.error("Export {} failed", jobId, e);
            deleteQuietly(part);
            deleteQuietly(target);
            jobService.markFailed(jobId, "Export failed");
        } finally {
            registry.remove(jobId);
        }
    }

    private static String csv(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        String cleaned = sanitize(value).strip();
        if (cleaned.isEmpty()) {
            return "";
        }
        String folded = Normalizer.normalize(cleaned, Normalizer.Form.NFKC).stripLeading();
        if (!folded.isEmpty() && FORMULA_PREFIXES.indexOf(folded.charAt(0)) >= 0) {
            cleaned = "'" + cleaned;
        }
        return '"' + cleaned.replace("\"", "\"\"") + '"';
    }

    private static String sanitize(String value) {
        StringBuilder sb = new StringBuilder(Math.min(value.length(), MAX_CELL_LENGTH));
        int count = 0;
        int i = 0;
        while (i < value.length() && count < MAX_CELL_LENGTH) {
            int cp = value.codePointAt(i);
            i += Character.charCount(cp);
            if (cp == '\n' || cp == '\r' || cp == '\t') {
                sb.append(' ');
                count++;
                continue;
            }
            switch (Character.getType(cp)) {
                case Character.SPACE_SEPARATOR -> {
                    sb.append(' ');
                    count++;
                }
                case Character.CONTROL, Character.FORMAT, Character.LINE_SEPARATOR,
                     Character.PARAGRAPH_SEPARATOR, Character.PRIVATE_USE,
                     Character.SURROGATE, Character.UNASSIGNED -> {
                }
                default -> {
                    sb.appendCodePoint(cp);
                    count++;
                }
            }
        }
        return sb.toString();
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
