package com.example.moneyflowapi.export;


import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record ExportRow(
        Instant date,
        String walletName,
        String walletType,
        String category,
        String type,
        String currencyCode,
        BigDecimal amount,
        BigDecimal rateAmount,
        String description
) {
}
