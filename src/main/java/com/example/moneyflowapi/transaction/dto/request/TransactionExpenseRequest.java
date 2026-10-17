package com.example.moneyflowapi.transaction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionExpenseRequest(
        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 14, fraction = 2)
        BigDecimal amount,

        @NotNull
        @JsonProperty("category_id")
        UUID categoryId,

        @NotNull
        @PastOrPresent
        Instant date,

        @Size(max = 500)
        String description
) {
}
