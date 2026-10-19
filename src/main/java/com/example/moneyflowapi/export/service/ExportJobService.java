package com.example.moneyflowapi.export.service;

import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.export.ExportFileNames;
import com.example.moneyflowapi.export.ExportFormat;
import com.example.moneyflowapi.export.dto.internal.ExportJobResult;
import com.example.moneyflowapi.export.entity.ExportJob;
import com.example.moneyflowapi.export.respository.ExportJobRepository;
import com.example.moneyflowapi.export.ExportStatus;
import com.example.moneyflowapi.export.config.ExportProperties;
import com.example.moneyflowapi.export.dto.response.ExportStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportJobService {
    private static final Duration MAX_RANGE = Duration.ofDays(367);

    private final ExportJobRepository repository;
    private final ExportProgressRegistry registry;
    private final ExportProperties properties;
    private final JdbcTemplate jdbcTemplate;

    private static final List<ExportStatus> REUSABLE =
            List.of(ExportStatus.PENDING, ExportStatus.RUNNING, ExportStatus.READY);

    @Transactional
    public ExportJobResult createOrReuse(UUID userId, Instant from, Instant to, ZoneId zone, ExportFormat format) {
        lockUser(userId);

        Instant now = Instant.now();
        Optional<ExportJob> existing = repository
                .findReusable(userId, from, to, zone.getId(), format, REUSABLE, now)
                .stream()
                .findFirst();

        if (existing.isPresent()) {
            return new ExportJobResult(existing.get(), false);
        }

        ExportJob job = create(userId, from, to, zone, format);
        return new ExportJobResult(job, true);
    }

    private ExportJob create(UUID userId, Instant from, Instant to, ZoneId zone, ExportFormat format) {
        if (!from.isBefore(to) || Duration.between(from, to).compareTo(MAX_RANGE) > 0) {
            throw new BusinessException(ErrorCode.EXPORTS_INVALID_DATE_RANGE);
        }

        Instant now = Instant.now();
        ExportJob job = new ExportJob();
        job.setUserId(userId);
        job.setStatus(ExportStatus.PENDING);
        job.setDateFrom(from);
        job.setDateTo(to);
        job.setZoneId(zone.getId());
        job.setFormat(format);
        job.setDownloadName(ExportFileNames.transactions(from, to, zone, Instant.now(), format));
        job.setExpiresAt(now.plus(Duration.ofHours(properties.ttlHours())));

        try {
            return repository.saveAndFlush(job);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.EXPORTS_ALREADY_HAVE_ACTIVE_EXPORT);
        }
    }


    public ExportJob getOwned(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPORTS_NOT_FOUND));
    }

    public ExportJob getOwnedReady(UUID id, UUID userId) {
        return requireReady(getOwned(id, userId));
    }

    public ExportJob getReady(UUID id) {
        ExportJob job = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPORTS_NOT_FOUND));
        return requireReady(job);
    }

    private ExportJob requireReady(ExportJob job) {
        if (job.getStatus() != ExportStatus.READY || job.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.EXPORTS_NOT_AVAILABLE);
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

    private void lockUser(UUID userId) {
        long key = userId.getMostSignificantBits() ^ userId.getLeastSignificantBits();
        jdbcTemplate.queryForObject(
                "SELECT count(*) FROM (SELECT pg_advisory_xact_lock(?)) t", Long.class, key);
    }
}
