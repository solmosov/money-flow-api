package com.example.moneyflowapi.export.service;

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
import java.time.Instant;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportRunner {
    private static final String HEADER = "date,category_name,type,amount,description\n";

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
    public void run(UUID jobId, UUID userId, Instant from, Instant to) {
        Path part = properties.path().resolve(jobId + ".csv.gz.part");
        Path target = properties.path().resolve(jobId + ".csv.gz");

        Timestamp tsFrom = Timestamp.from(from);
        Timestamp tsTo = Timestamp.from(to);

        try {
            Long total = jdbcTemplate.queryForObject(COUNT_SQL, Long.class, userId, tsFrom, tsTo);
            ExportProgressRegistry.Progress progress = registry.start(jobId, total == null ? 0 : total);
            jobService.markRunning(jobId);

            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            tx.setReadOnly(true);

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new GZIPOutputStream(Files.newOutputStream(part), 65536), StandardCharsets.UTF_8), 65536)) {

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
                            BigDecimal amount = rs.getBigDecimal(4);
                            writer.write(rs.getTimestamp(1).toInstant() + ","
                                    + csv(rs.getString(2)) + ","
                                    + csv(rs.getString(3)) + ","
                                    + (amount == null ? "" : amount.toPlainString()) + ","
                                    + csv(rs.getString(5)) + "\n");
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }

                        progress.processed().incrementAndGet();
                    });
                });
            }

            Files.move(part, target, StandardCopyOption.ATOMIC_MOVE);
            jobService.markReady(jobId, target.getFileName().toString(), Files.size(target));
        } catch (IOException e) {
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
        char first = value.charAt(0);
        if (first == '=' || first == '+' || first == '-' || first == '@'
                || first == '\t' || first == '\r') {
            value = "'" + value;
        }
        boolean needsQuotes = value.indexOf(',') >= 0 || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0;
        return needsQuotes ? '"' + value.replace("\"", "\"\"") + '"' : value;
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
