package com.example.moneyflowapi.transaction.enums;

public enum StatPeriod {
    WEEKLY, MONTHLY, YEARLY;

    public String sqlValue() {
        return switch (this) {
            case WEEKLY -> "week";
            case MONTHLY -> "month";
            case YEARLY -> "year";
        };
    }
}