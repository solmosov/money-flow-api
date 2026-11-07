package com.example.moneyflowapi.telegram.parser;

import com.example.moneyflowapi.currency.CurrencyCode;
import com.example.moneyflowapi.transaction.TransactionType;

import java.math.BigDecimal;

public record ParsedTransaction(
        TransactionType type,
        String category,
        BigDecimal amount,
        CurrencyCode currency,
        String note
) {
}