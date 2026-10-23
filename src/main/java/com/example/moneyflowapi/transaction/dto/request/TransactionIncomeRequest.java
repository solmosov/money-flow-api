package com.example.moneyflowapi.transaction.dto.request;

import com.example.moneyflowapi.common.annotation.SafeText;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Request to create an income transaction")
public record TransactionIncomeRequest(
        @Schema(description = "Account ID", example = "f540d69b-28f1-4c7d-81b5-a1a7819ff0f9")
        @NotNull
        UUID walletId,

        @Schema(description = "Income category ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull
        @JsonProperty("category_id")
        UUID categoryId,


        @Schema(description = "Income amount", example = "1500.00")
        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 14, fraction = 2)
        BigDecimal amount,

        @Schema(description = "Date and time of the income (not in the future)", example = "2026-10-21T09:30:00Z")
        @NotNull
        @PastOrPresent
        Instant date,

        @Schema(description = "Optional note", example = "October salary", nullable = true)
        @SafeText(max = 500)
        String description
) {
    public TransactionIncomeRequest {
        description = description == null || description.isBlank() ? null : description.strip();
    }
}