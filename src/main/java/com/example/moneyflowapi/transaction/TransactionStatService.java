package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatsByIntervalResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionIncomeExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionMonthTotalsStatResponse;
import com.example.moneyflowapi.transaction.enums.StatsType;

import java.time.Instant;
import java.time.ZoneId;

public interface TransactionStatService {
    TransactionIncomeExpenseStatResponse getIncomeExpenseStats(StatsType granularity, Instant from, ZoneId zoneId);

    TransactionCategoryExpenseStatResponse getCategoryExpenseStats(StatsType type, Instant from, ZoneId zoneId);

    TransactionMonthTotalsStatResponse getMonthTotalsStats(Instant from, String timezone);
}
