package com.example.moneyflowapi.wallet.dto.request;

import com.example.moneyflowapi.currency.CurrencyCode;
import com.example.moneyflowapi.wallet.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request to create an account")
public record CreateWalletRequest(
        @Schema(description = "Account name", example = "Main card", maxLength = 50)
        @NotBlank
        @Size(max = 50)
        String name,

        @Schema(description = "Account type")
        @NotNull
        WalletType type,

        @Schema(description = "ISO 4217 currency code", example = "UZS")
        @NotBlank
        @Size(min = 3, max = 3)
        CurrencyCode currencyCode,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 10)
        BigDecimal rateAmount,

        @Schema(description = "Initial balance", example = "1000000.00")
        @NotNull
        @PositiveOrZero
        @Digits(integer = 12, fraction = 2)
        BigDecimal initialBalance
) {
}