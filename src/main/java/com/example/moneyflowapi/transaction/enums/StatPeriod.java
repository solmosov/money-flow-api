package com.example.moneyflowapi.transaction.enums;

public enum StatPeriod {
    WEEK, MONTH, YEAR;

    public String sqlValue() {
        return name().toLowerCase();
    }
}