package com.example.moneyflowapi.transaction.dto.request;

import com.example.moneyflowapi.transaction.TransactionCurrency;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionExpenseRequest(
        BigDecimal amount,
        TransactionCurrency currency,

        @JsonProperty("category_id")
        UUID categoryId,
        Instant date,
        String description
) {
}
