package com.example.moneyflowapi.transaction.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record TransactionCategoryExpenseStatResponse(
        List<Item> data
) {
    @Builder
    public record Item(
            BigDecimal value,
            String name
    ) {
    }
}
