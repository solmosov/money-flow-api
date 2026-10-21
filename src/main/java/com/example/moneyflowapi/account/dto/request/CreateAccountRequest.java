package com.example.moneyflowapi.account.dto.request;

import com.example.moneyflowapi.account.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request to create an account")
public record CreateAccountRequest(
        @Schema(description = "Account name", example = "Main card", maxLength = 50)
        @NotBlank
        @Size(max = 50)
        String name,

        @Schema(description = "Account type")
        @NotNull
        AccountType type,

        @Schema(description = "ISO 4217 currency code", example = "UZS")
        @NotBlank
        @Size(min = 3, max = 3)
        String currencyCode,

        @NotNull
        @Positive
        @Digits(integer = 8, fraction = 2)
        BigDecimal rateAmount,

        @Schema(description = "Initial balance", example = "1000000.00")
        @NotNull
        @PositiveOrZero
        @Digits(integer = 12, fraction = 2)
        BigDecimal initialBalance
) {
}