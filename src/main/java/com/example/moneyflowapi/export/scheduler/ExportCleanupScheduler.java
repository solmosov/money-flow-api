package com.example.moneyflowapi.export.scheduler;

import com.example.moneyflowapi.export.entity.ExportJob;
import com.example.moneyflowapi.export.respository.ExportJobRepository;
import com.example.moneyflowapi.export.ExportStatus;
import com.example.moneyflowapi.export.config.ExportProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportCleanupScheduler {

    private final ExportJobRepository repository;
    private final ExportProperties properties;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        List<ExportJob> expired = repository.findTop200ByStatusInAndExpiresAtBefore(
                Set.of(ExportStatus.READY, ExportStatus.FAILED), Instant.now());

        for (ExportJob job : expired) {
            if (job.getFileName() != null) {
                try {
                    Files.deleteIfExists(properties.path().resolve(job.getFileName()));
                } catch (IOException e) {
                    log.warn("Could not delete export file {}", job.getFileName(), e);
                }
            }
        }

        repository.deleteAll(expired);
    }
}
