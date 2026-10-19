package com.example.moneyflowapi.export.service;

import com.example.moneyflowapi.export.*;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
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

    private final Map<ExportFormat, ExportWriterFactory> writers;

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;
    private final ExportJobService jobService;
    private final ExportProgressRegistry registry;
    private final ExportProperties properties;

    public ExportRunner(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager,
                        ExportJobService jobService, ExportProgressRegistry registry,
                        ExportProperties properties, List<ExportWriterFactory> factories) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionManager = transactionManager;
        this.jobService = jobService;
        this.registry = registry;
        this.properties = properties;
        this.writers = factories.stream()
                .collect(Collectors.toMap(ExportWriterFactory::format, Function.identity()));
    }

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(properties.path());
    }

    @Async("exportExecutor")
    public void run(UUID jobId, UUID userId, Instant from, Instant to, ZoneId zone, String downloadName, ExportFormat format) {

        String ext = format.extension();
        Path part = properties.path().resolve(jobId + "." + ext + ".part");
        Path target = properties.path().resolve(jobId + "." + ext);

        Timestamp tsFrom = Timestamp.from(from);
        Timestamp tsTo = Timestamp.from(to);

        try {
            Long total = jdbcTemplate.queryForObject(COUNT_SQL, Long.class, userId, tsFrom, tsTo);
            ExportProgressRegistry.Progress progress = registry.start(jobId, total == null ? 0 : total);
            jobService.markRunning(jobId);

            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            tx.setReadOnly(true);

            try (OutputStream out = Files.newOutputStream(part);
                 ExportWriter writer = writers.get(format).create(out, zone, downloadName)) {

                writer.begin();

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
                            writer.write(new ExportRow(
                                    rs.getTimestamp(1).toInstant(),
                                    rs.getString(2),
                                    rs.getString(3),
                                    rs.getBigDecimal(4),
                                    rs.getString(5)
                            ));

                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }

                        progress.processed().incrementAndGet();
                    });
                });

                writer.end();
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

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
