package com.example.moneyflowapi.transaction.dto.request;

import com.example.moneyflowapi.common.annotation.SafeText;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionTransferRequest(
        @Schema(description = "Wallet ID", example = "f540d69b-28f1-4c7d-81b5-a1a7819ff0f9")
        @NotNull
        UUID fromWalletId,

        @Schema(description = "To Wallet ID", example = "f540d69b-28f1-4c7d-81b5-a1a7819ff0f9")
        @NotNull
        UUID toWalletId,

        @Schema(description = "Transfer amount", example = "1500.00")
        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 14, fraction = 2)
        BigDecimal fromAmount,

        @Schema(description = "Transfer amount", example = "1500.00")
        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 14, fraction = 2)
        BigDecimal toAmount,

        @Schema(description = "Date and time of the income (not in the future)", example = "2026-10-21T09:30:00Z")
        @NotNull
        @PastOrPresent
        Instant date,

        @Schema(description = "Optional note", example = "October salary", nullable = true)
        @SafeText(max = 500)
        String description
) {
}
