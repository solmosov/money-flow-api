package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionIncomeExpenseStatResponse;
import com.example.moneyflowapi.transaction.enums.StatsType;

import java.time.Instant;

public interface TransactionStatService {
    TransactionIncomeExpenseStatResponse getIncomeExpenseStats(StatsType granularity, Instant from, String timezone);

    TransactionCategoryExpenseStatResponse getCategoryExpenseStats(StatsType type, Instant from, String timezone);
}
