package com.example.moneyflowapi.account.dto.request;

import com.example.moneyflowapi.account.AccountType;
import com.example.moneyflowapi.currency.Currency;

import java.math.BigDecimal;

public record CreateAccountRequest(
        String name,
        AccountType type,
        String currencyCode,
        BigDecimal initialBalance
) {
}
