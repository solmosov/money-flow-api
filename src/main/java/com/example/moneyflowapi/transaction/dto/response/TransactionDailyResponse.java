package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record TransactionDailyResponse(
        Instant day,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal total,
        List<Item> items
) {
    public record  Item(
            String categoryName,
            String categoryIconName,
            BigDecimal amount,
            String currency,
            String description,
            TransactionType type,
            Instant createdAt,
            Instant updatedAt
    ){}
}
