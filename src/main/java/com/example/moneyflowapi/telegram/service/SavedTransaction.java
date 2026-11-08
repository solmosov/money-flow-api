package com.example.moneyflowapi.telegram.service;

import com.example.moneyflowapi.currency.CurrencyCode;
import com.example.moneyflowapi.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record SavedTransaction(
        UUID id,
        TransactionType type,
        String category,
        BigDecimal amount,
        CurrencyCode currency,
        String walletName,
        String note
) {
}