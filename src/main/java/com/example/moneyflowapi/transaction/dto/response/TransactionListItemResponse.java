package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record TransactionListItemResponse(
        UUID id,
        String walletName,
        String walletType,
        String categoryName,
        String categoryIconName,
        BigDecimal amount,
        String currencyCode,
        String currencySymbol,
        String description,
        TransactionType type,
        Instant date,
        Instant createdAt,
        Instant updatedAt
) {}