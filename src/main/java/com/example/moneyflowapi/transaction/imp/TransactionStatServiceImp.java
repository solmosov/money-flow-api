package com.example.moneyflowapi.transaction.imp;

import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.TransactionRepository;
import com.example.moneyflowapi.transaction.TransactionStatService;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionIncomeExpenseStatResponse;
import com.example.moneyflowapi.transaction.enums.StatsType;
import com.example.moneyflowapi.transaction.projection.TransactionIncomeExpenseStatProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionStatServiceImp implements TransactionStatService {

    private final TransactionRepository transactionRepo;

    @Override
    public TransactionIncomeExpenseStatResponse getIncomeExpenseStats(StatsType type, Instant from, String timezone) {

        UUID userId = SecurityUtil.getCurrentUserId();

        ZoneId zoneId = ZoneId.of(timezone);

        var stats = transactionRepo.findIncomeExpenseStats(
                userId,
                type.getUnit(),
                type.getInterval(),
                from,
                zoneId
        );

        List<String> labels = stats.stream()
                .map(s -> s.getDate().toString())
                .toList();

        List<BigDecimal> incomes = stats.stream()
                .map(TransactionIncomeExpenseStatProjection::getIncome)
                .toList();

        List<BigDecimal> expenses = stats.stream()
                .map(TransactionIncomeExpenseStatProjection::getExpense)
                .toList();

        var seriesIncome = TransactionIncomeExpenseStatResponse.Series.builder()
                .key(TransactionType.INCOME)
                .name(TransactionType.INCOME.name())
                .data(incomes)
                .build();

        var seriesExpenses = TransactionIncomeExpenseStatResponse.Series.builder()
                .key(TransactionType.EXPENSE)
                .name(TransactionType.EXPENSE.name())
                .data(expenses)
                .build();

        return TransactionIncomeExpenseStatResponse.builder()
                .period(TransactionIncomeExpenseStatResponse.Period.builder()
                        .from(from)
                        .type(type)
                        .build())
                .currency("UZS")
                .labels(labels)
                .series(List.of(
                        seriesIncome,
                        seriesExpenses
                ))
                .build();
    }

    @Override
    public TransactionCategoryExpenseStatResponse getCategoryExpenseStats(StatsType type, Instant from) {
        UUID userId = SecurityUtil.getCurrentUserId();

        var stats = transactionRepo.findCategoryExpenseStats(
                userId,
                type.getUnit(),
                type.getInterval(),
                from
        );

        var data = stats.stream()
                .map(row -> TransactionCategoryExpenseStatResponse.Item.builder()
                        .value(row.getValue())
                        .name(row.getName())
                        .build()
                )
                .toList();

        return TransactionCategoryExpenseStatResponse.builder()
                .data(data)
                .build();
    }
}
