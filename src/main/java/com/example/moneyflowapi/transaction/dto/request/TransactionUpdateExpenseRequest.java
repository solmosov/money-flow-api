package com.example.moneyflowapi.transaction.dto.request;

import com.example.moneyflowapi.common.annotation.SafeText;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Request to update an expense transaction. Only provided fields are updated")
public record TransactionUpdateExpenseRequest(
        @Schema(description = "Expense category ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", nullable = true)
        @JsonProperty("category_id")
        UUID categoryId,

        @Schema(description = "Expense amount", example = "250.00", nullable = true)
        @DecimalMin(value = "0.01")
        @Digits(integer = 14, fraction = 2)
        BigDecimal amount,

        @Schema(description = "Date and time of the expense (not in the future)", example = "2026-10-21T09:30:00Z", nullable = true)
        @PastOrPresent
        Instant date,

        @Schema(description = "Optional note", example = "Groceries", nullable = true)
        @SafeText(max = 500)
        String description
) {
    public TransactionUpdateExpenseRequest {
    }
}