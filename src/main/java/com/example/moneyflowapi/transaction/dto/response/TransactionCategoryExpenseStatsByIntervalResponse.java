package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.enums.StatsType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record TransactionCategoryExpenseStatsByIntervalResponse(
        Instant from,
        StatsType type,
        List<Item> items
) {

    @Builder
    public record Item(
            Instant date,
            String categoryName,
            String categoryIconName,
            BigDecimal totalExpense
    ) {
    }
}
