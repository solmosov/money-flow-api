package com.example.moneyflowapi.transaction.imp;

import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.TransactionRepository;
import com.example.moneyflowapi.transaction.TransactionStatService;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatsByIntervalResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionIncomeExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionMonthTotalsStatResponse;
import com.example.moneyflowapi.transaction.enums.StatsType;
import com.example.moneyflowapi.transaction.projection.TransactionIncomeExpenseStatProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionStatServiceImp implements TransactionStatService {

    private final TransactionRepository transactionRepo;

    @Override
    public TransactionIncomeExpenseStatResponse getIncomeExpenseStats(StatsType type, Instant from, ZoneId zoneId) {

        UUID userId = SecurityUtil.getCurrentUserId();

        ZonedDateTime start = truncate(from.atZone(zoneId), type.getStep());
        ZonedDateTime end = start.plus(type.getPeriod());

        Map<Instant, TransactionIncomeExpenseStatProjection> byDate = transactionRepo
                .findIncomeExpenseStats(
                        userId,
                        type.getUnit(),
                        start.toInstant(),
                        end.toInstant(),
                        zoneId.getId()
                )
                .stream()
                .collect(Collectors.toMap(
                        TransactionIncomeExpenseStatProjection::getDate,
                        Function.identity()
                ));

        List<String> labels = new ArrayList<>();
        List<BigDecimal> incomes = new ArrayList<>();
        List<BigDecimal> expenses = new ArrayList<>();

        for (ZonedDateTime cursor = start; cursor.isBefore(end); cursor = cursor.plus(1, type.getStep())) {
            TransactionIncomeExpenseStatProjection row = byDate.get(cursor.toInstant());

            labels.add(cursor.toInstant().toString());
            incomes.add(row == null ? BigDecimal.ZERO : row.getIncome());
            expenses.add(row == null ? BigDecimal.ZERO : row.getExpense());
        }

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
                .series(List.of(seriesIncome, seriesExpenses))
                .build();
    }

    private ZonedDateTime truncate(ZonedDateTime value, ChronoUnit step) {
        ZonedDateTime day = value.toLocalDate().atStartOfDay(value.getZone());
        return step == ChronoUnit.MONTHS ? day.withDayOfMonth(1) : day;
    }

    @Override
    public TransactionCategoryExpenseStatResponse getCategoryExpenseStats(StatsType type, Instant from, ZoneId zoneId) {
        UUID userId = SecurityUtil.getCurrentUserId();

        ZonedDateTime start = truncate(from.atZone(zoneId), type.getStep());
        ZonedDateTime end = start.plus(type.getPeriod());

        var stats = transactionRepo.findCategoryExpenseStats(
                        userId,
                        start.toInstant(),
                        end.toInstant()
                )
                .stream()
                .map(row -> TransactionCategoryExpenseStatResponse.Item.builder()
                        .value(row.getValue())
                        .name(row.getName())
                        .build())
                .toList();

        return TransactionCategoryExpenseStatResponse.builder()
                .data(stats)
                .build();
    }

    @Override
    public TransactionMonthTotalsStatResponse getMonthTotalsStats(Instant from, String timezone) {
        UUID userId = SecurityUtil.getCurrentUserId();

        ZoneId zoneId = ZoneId.of(timezone);

        var stat = transactionRepo.findMonthTotalsStat(
                userId,
                from,
                zoneId
        );

        return TransactionMonthTotalsStatResponse.builder()
                .from(from)
                .month(stat.getMonth())
                .totalIncome(stat.getTotalIncome())
                .totalExpense(stat.getTotalExpense())
                .total(stat.getTotal())
                .build();
    }
}
