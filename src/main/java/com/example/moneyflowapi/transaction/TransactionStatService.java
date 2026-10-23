package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionDiffStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionIncomeExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionMonthTotalsStatResponse;
import com.example.moneyflowapi.transaction.enums.StatsType;

import java.time.Instant;
import java.time.ZoneId;

public interface TransactionStatService {
    TransactionIncomeExpenseStatResponse getIncomeExpenseStats(StatsType granularity, Instant from, ZoneId zoneId);

    TransactionCategoryExpenseStatResponse getCategoryTotalsStatsByType(StatsType statsType, CategoryType categoryType, Instant from, ZoneId zoneId);

    TransactionMonthTotalsStatResponse getMonthTotalsStats(Instant from, ZoneId zoneId);

    TransactionDiffStatResponse getDiffStats(Instant from, ZoneId zoneId);
}
