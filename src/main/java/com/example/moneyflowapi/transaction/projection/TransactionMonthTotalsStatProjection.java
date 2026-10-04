package com.example.moneyflowapi.transaction.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionMonthTotalsStatProjection {
    Instant getMonth();
    BigDecimal getTotalIncome();
    BigDecimal getTotalExpense();
    BigDecimal getTotal();
}
