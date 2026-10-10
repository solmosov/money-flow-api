package com.example.moneyflowapi.transaction.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record DailyTotalResponse(
        Instant day,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal total
) {

}
