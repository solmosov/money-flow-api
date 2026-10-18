package com.example.moneyflowapi.export.service;

import com.example.moneyflowapi.export.ExportJobRepository;
import com.example.moneyflowapi.export.ExportStatus;
import com.example.moneyflowapi.export.config.ExportProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportStartupRecovery {

    private final ExportJobRepository repository;
    private final ExportProperties properties;

    @EventListener(ApplicationReadyEvent.class)
    public void recover() {
        repository.failActive(ExportStatus.FAILED, Set.of(ExportStatus.PENDING, ExportStatus.RUNNING), Instant.now());

        try (DirectoryStream<Path> parts = Files.newDirectoryStream(properties.path(), "*.part")) {
            for (Path part : parts) {
                Files.deleteIfExists(part);
            }
        } catch (IOException e) {
            log.warn("Could not clean leftover .part files", e);
        }
    }
}
