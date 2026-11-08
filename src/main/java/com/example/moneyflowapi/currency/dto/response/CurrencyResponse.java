package com.example.moneyflowapi.currency.dto.response;

import com.example.moneyflowapi.currency.CurrencyCode;
import lombok.Builder;

@Builder
public record CurrencyResponse(
        CurrencyCode code,
        String name,
        String symbol,
        int decimal,
        boolean isActive
) {
}
