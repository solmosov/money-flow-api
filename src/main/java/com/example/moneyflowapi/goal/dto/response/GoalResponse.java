package com.example.moneyflowapi.goal.dto.response;

import com.example.moneyflowapi.goal.GoalStatus;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record GoalResponse(
        UUID id,
        String name,
        BigDecimal targetAmount,
        String currencyCode,
        BigDecimal rateSnapshot,
        BigDecimal baseAmountSnapshot,
        BigDecimal startingAmount,
        Instant deadline,
        GoalStatus status,
        Instant completedAt,
        Instant createdAt,
        Instant updateAt,
        WalletResponse wallet
) {
}