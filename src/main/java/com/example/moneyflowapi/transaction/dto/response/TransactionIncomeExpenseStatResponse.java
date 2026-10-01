package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.transaction.enums.StatsType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record TransactionIncomeExpenseStatResponse(
        Period period,
        String currency,
        List<String> labels,
        List<Series> series
) {

    @Builder
    public record Period(
            Instant from,
//            Instant to,
            StatsType type
    ) {

    }

    @Builder
    public record Series(
      TransactionType key,
      String name,
      List<BigDecimal> data
    ){}


}
