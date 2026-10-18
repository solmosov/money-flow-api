package com.example.moneyflowapi.export.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ExportRequest(
        @NotNull Instant from,
        @NotNull Instant to
) {
}
