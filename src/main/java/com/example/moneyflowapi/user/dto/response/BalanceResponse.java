package com.example.moneyflowapi.user.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal balance
) {
}
