package com.example.moneyflowapi.transaction.enums;

import lombok.Getter;

@Getter
public enum StatsType {
    WEEKLY("day", "1 week"),
    MONTHLY("week", "1 month"),
    YEARLY("month", "1 year");

    private final String value;
    private final String interval;

    StatsType(String value, String interval) {
        this.value = value;
        this.interval = interval;
    }

}
