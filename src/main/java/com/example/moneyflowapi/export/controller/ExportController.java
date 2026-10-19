package com.example.moneyflowapi.export.controller;

import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.export.ExportFileNames;
import com.example.moneyflowapi.export.ExportFormat;
import com.example.moneyflowapi.export.dto.internal.ExportJobResult;
import com.example.moneyflowapi.export.entity.ExportJob;
import com.example.moneyflowapi.export.dto.request.ExportRequest;
import com.example.moneyflowapi.export.dto.response.ExportStatusResponse;
import com.example.moneyflowapi.export.service.ExportFileChecker;
import com.example.moneyflowapi.export.service.ExportJobService;
import com.example.moneyflowapi.export.service.ExportLinkSigner;
import com.example.moneyflowapi.export.service.ExportRunner;
import com.example.moneyflowapi.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exports")
public class ExportController {

    static final String BASE_PATH = "/api/v1/exports";
    private static final long LINK_TTL_SECONDS = 600;

    private final ExportJobService jobService;
    private final ExportRunner runner;
    private final ExportLinkSigner signer;
    private final ExportFileChecker fileChecker;

    @PostMapping
    public ResponseEntity<Map<String, UUID>> create(
            @Valid @RequestBody ExportRequest request,
            @RequestHeader(value = "X-Timezone", defaultValue = "UTC") String timezone
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();
        ZoneId zone = ExportFileNames.parseZone(timezone);

        Instant from = request.from().truncatedTo(ChronoUnit.MINUTES);
        Instant to = request.to().truncatedTo(ChronoUnit.MINUTES);

        ExportJobResult result = jobService.createOrReuse(userId, from, to, zone, request.format());
        ExportJob job = result.job();

        if (result.created()) {
            try {
                runner.run(job.getId(), userId, job.getDateFrom(), job.getDateTo(), zone, job.getDownloadName(), job.getFormat());
            } catch (TaskRejectedException e) {
                jobService.markFailed(job.getId(), "Export queue is full");
                throw new BusinessException(ErrorCode.EXPORTS_TOO_MANY_REQUESTS);
            }
        }

        return ResponseEntity.accepted().body(Map.of("id", job.getId()));
    }

    @GetMapping("/{id}/status")
    public ExportStatusResponse status(@PathVariable UUID id) {
        return jobService.getStatus(id, SecurityUtil.getCurrentUserId());
    }

    @PostMapping("/{id}/link")
    public Map<String, String> link(@PathVariable UUID id) {
        jobService.getOwnedReady(id, SecurityUtil.getCurrentUserId());

        long exp = Instant.now().plusSeconds(LINK_TTL_SECONDS).getEpochSecond();
        String sig = signer.sign(id, exp);
        return Map.of("download_url",
                BASE_PATH + "/" + id + "/file?exp=" + exp + "&sig=" + sig);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Void> file(@PathVariable UUID id,
                                     @RequestParam long exp,
                                     @RequestParam String sig) {
        if (!signer.verify(id, exp, sig)) {
            throw new BusinessException(ErrorCode.EXPORTS_INVALID_OR_EXPIRED_LINK);
        }

        ExportJob job = jobService.getReady(id);
        fileChecker.ensureServable(job.getFileName());
        ExportFormat format = job.getFormat();
        String name = job.getDownloadName() != null ? job.getDownloadName() : ExportFileNames.BRAND_NAME + "_transactions." + format.extension();

        return ResponseEntity.ok()
                .header("X-Accel-Redirect", "/protected-exports/" + job.getFileName())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(name).build().toString())
                .header(HttpHeaders.CONTENT_TYPE, format.contentType())
                .build();
    }
}
