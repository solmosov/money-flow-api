package com.example.moneyflowapi.transaction.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionCategoryExpenseStatsByIntervalProjection {
    Instant getDate();
    String getCategoryName();
    String getCategoryIconName();
    BigDecimal getTotalExpense();
}
