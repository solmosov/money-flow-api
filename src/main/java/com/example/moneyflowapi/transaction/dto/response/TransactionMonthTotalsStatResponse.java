package com.example.moneyflowapi.transaction.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record TransactionMonthTotalsStatResponse(
        Instant from,
        Instant month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal total
) {
}
