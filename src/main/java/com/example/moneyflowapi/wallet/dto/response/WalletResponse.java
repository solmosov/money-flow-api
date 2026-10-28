package com.example.moneyflowapi.wallet.dto.response;

import com.example.moneyflowapi.wallet.WalletType;
import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record WalletResponse(
        UUID id,
        String name,
        WalletType type,
        CurrencyResponse currency,
        BigDecimal balance,
        BigDecimal rateAmount,
        Instant archivedAt,
        boolean isRateChangeConfirmed,
        Instant createdAt,
        Instant updatedAt
) {
}
