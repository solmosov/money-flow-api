package com.example.moneyflowapi.transaction.projection;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Instant;

public interface DailyTotalProjection {

    Instant getDay();

    @Value("#{target.total_income}")
    BigDecimal getTotalIncome();

    @Value("#{target.total_expense}")
    BigDecimal getTotalExpense();
}
