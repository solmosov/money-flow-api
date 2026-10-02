package com.example.moneyflowapi.transaction.projection;

import java.math.BigDecimal;

public interface TransactionCategoryExpenseStatProjection {
    BigDecimal getValue();
    String getName();
}
