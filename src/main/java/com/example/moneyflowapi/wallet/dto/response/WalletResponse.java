package com.example.moneyflowapi.wallet.dto.response;

import com.example.moneyflowapi.wallet.WalletType;
import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// TODO: add goal
@Builder
public record WalletResponse(
        UUID id,
        String name,
        WalletType type,
        CurrencyResponse currency,
        BigDecimal balance,
        Instant archivedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
