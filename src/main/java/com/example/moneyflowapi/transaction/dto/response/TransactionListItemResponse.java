package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionListItemResponse(
        UUID id,
        String accountName,
        String accountType,
        String categoryName,
        String categoryIconName,
        BigDecimal amount,
        String currencyCode,
        String description,
        TransactionType type,
        Instant date,
        Instant createdAt,
        Instant updatedAt
) {}