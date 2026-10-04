package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record TransactionDailyResponse(
        Instant day,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal total,
        List<Item> items
) {
    public record  Item(
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
    ){}
}
