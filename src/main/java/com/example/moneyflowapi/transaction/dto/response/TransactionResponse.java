package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record TransactionResponse(
        UUID id,
        WalletResponse wallet,
        WalletResponse toWallet,
        CategoryResponse category,
        CurrencyResponse currency,
        BigDecimal amount,
        BigDecimal toAmount,
        BigDecimal rateAmount,
        String description,
        TransactionType type,
        Instant date,

        Instant createdAt,
        Instant updatedAt
) {
}
