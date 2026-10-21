package com.example.moneyflowapi.account.dto.response;

import com.example.moneyflowapi.account.AccountType;
import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// TODO: add goal
@Builder
public record AccountResponse(
        UUID id,
        String name,
        AccountType type,
        CurrencyResponse currency,
        BigDecimal balance,
        Instant archivedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
