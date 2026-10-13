package com.example.moneyflowapi.transaction.enums;

import lombok.Getter;

import java.time.Period;
import java.time.temporal.ChronoUnit;

@Getter
public enum StatsType {
    WEEKLY("day", ChronoUnit.DAYS, Period.ofWeeks(1)),
    MONTHLY("day", ChronoUnit.DAYS, Period.ofMonths(1)),
    YEARLY("month", ChronoUnit.MONTHS, Period.ofYears(1));

    private final String unit;
    private final ChronoUnit step;
    private final Period period;

    StatsType(String unit, ChronoUnit step, Period period) {
        this.unit = unit;
        this.step = step;
        this.period = period;
    }

}
