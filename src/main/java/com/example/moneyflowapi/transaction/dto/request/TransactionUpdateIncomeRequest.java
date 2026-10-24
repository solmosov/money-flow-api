package com.example.moneyflowapi.transaction.dto.request;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionUpdateIncomeRequest(
        UUID categoryId,
        BigDecimal amount,
        Instant date,
        String description
) {
}
