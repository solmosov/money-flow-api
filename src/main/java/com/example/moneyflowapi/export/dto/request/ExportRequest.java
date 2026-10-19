package com.example.moneyflowapi.export.dto.request;

import com.example.moneyflowapi.export.ExportFormat;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ExportRequest(
        ExportFormat format,
        @NotNull Instant from,
        @NotNull Instant to
) {
    public ExportRequest {
        if (format == null) {
            format = ExportFormat.CSV;
        }
    }
}
