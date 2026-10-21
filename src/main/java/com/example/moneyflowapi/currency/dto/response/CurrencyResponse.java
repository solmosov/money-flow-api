package com.example.moneyflowapi.currency.dto.response;

import lombok.Builder;

@Builder
public record CurrencyResponse(
        String code,
        String name,
        String symbol,
        int decimal,
        boolean isActive
) {
}
