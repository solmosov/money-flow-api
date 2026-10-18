package com.example.moneyflowapi.export.service;

import com.example.moneyflowapi.export.ExportJob;
import com.example.moneyflowapi.export.ExportJobRepository;
import com.example.moneyflowapi.export.ExportStatus;
import com.example.moneyflowapi.export.config.ExportProperties;
import com.example.moneyflowapi.export.dto.response.ExportStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportJobService {
    private static final Duration MAX_RANGE = Duration.ofDays(367);

    private final ExportJobRepository repository;
    private final ExportProgressRegistry registry;
    private final ExportProperties properties;


    public ExportJob create(UUID userId, Instant from, Instant to) {
        if (!from.isBefore(to) || Duration.between(from, to).compareTo(MAX_RANGE) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range");
        }

        Instant now = Instant.now();
        ExportJob job = new ExportJob();
        job.setUserId(userId);
        job.setStatus(ExportStatus.PENDING);
        job.setDateFrom(from);
        job.setDateTo(to);
        job.setExpiresAt(now.plus(Duration.ofHours(properties.ttlHours())));

        try {
            return repository.saveAndFlush(job);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "You already have an active export");
        }
    }

    public ExportJob getOwned(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Export not found"));
    }

    public ExportJob getOwnedReady(UUID id, UUID userId) {
        return requireReady(getOwned(id, userId));
    }

    public ExportJob getReady(UUID id) {
        ExportJob job = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Export not found"));
        return requireReady(job);
    }

    private ExportJob requireReady(ExportJob job) {
        if (job.getStatus() != ExportStatus.READY || job.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Export is not available");
        }
        return job;
    }

    public ExportStatusResponse getStatus(UUID id, UUID userId) {
        ExportJob job = getOwned(id, userId);

        int percent = switch (job.getStatus()) {
            case READY -> 100;
            case RUNNING -> {
                ExportProgressRegistry.Progress progress = registry.get(id);
                if (progress == null || progress.total() == 0) {
                    yield 0;
                }
                yield (int) Math.min(99, progress.processed().get() * 100 / progress.total());
            }
            default -> 0;
        };

        return new ExportStatusResponse(job.getStatus().name(), percent, job.getErrorMessage());
    }

    public void markRunning(UUID id) {
        repository.updateStatus(id, ExportStatus.RUNNING);
    }

    public void markReady(UUID id, String fileName, long fileSize) {
        Instant now = Instant.now();
        repository.markReady(id, ExportStatus.READY, fileName, fileSize, now,
                now.plus(Duration.ofHours(properties.ttlHours())));
    }

    public void markFailed(UUID id, String message) {
        repository.markFailed(id, ExportStatus.FAILED, message, Instant.now());
    }
}
