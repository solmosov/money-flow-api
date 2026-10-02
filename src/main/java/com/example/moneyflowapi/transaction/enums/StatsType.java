package com.example.moneyflowapi.transaction.enums;

import lombok.Getter;

@Getter
public enum StatsType {
    WEEKLY("day", "1 week"),
    MONTHLY("day", "1 month"),
    YEARLY("month", "1 year");

    private final String unit;
    private final String interval;

    StatsType(String unit, String interval) {
        this.unit = unit;
        this.interval = interval;
    }

}
