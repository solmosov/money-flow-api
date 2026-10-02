package com.example.moneyflowapi.transaction.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionDailyProjection {
    Instant getDay();
    BigDecimal getTotalIncome();
    BigDecimal getTotalExpense();
    BigDecimal getTotal();
    String getTransactions();
}
