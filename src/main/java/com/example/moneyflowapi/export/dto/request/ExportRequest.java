package com.example.moneyflowapi.export.dto.request;

import com.example.moneyflowapi.export.ExportFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "Request to export transactions for a period")
public record ExportRequest(
        @Schema(description = "Export file format", defaultValue = "CSV", nullable = true)
        ExportFormat format,

        @Schema(description = "Period start (inclusive)", example = "2026-10-01T00:00:00Z")
        @NotNull
        Instant from,

        @Schema(description = "Period end", example = "2026-10-31T23:59:59Z")
        @NotNull
        Instant to
) {
    public ExportRequest {
        if (format == null) {
            format = ExportFormat.CSV;
        }
    }
}