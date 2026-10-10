package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionListItemResponse(
        UUID id,
        String categoryName,
        String categoryIconName,
        BigDecimal amount,
        String currency,
        String description,
        TransactionType type,
        Instant date,
        Instant createdAt,
        Instant updatedAt
) {}