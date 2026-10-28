package com.example.moneyflowapi.user.dto.response;

import com.example.moneyflowapi.currency.Currency;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal balance,
        Currency baseCurrency,
        Boolean isRateChangeConfirmed
) {
}
