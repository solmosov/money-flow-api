package com.example.moneyflowapi.account.dto.request;

import com.example.moneyflowapi.account.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

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
        @JsonProperty("currency_code")
        String currencyCode,

        @Schema(description = "Initial balance", example = "1000000.00")
        @NotNull
        @PositiveOrZero
        @Digits(integer = 12, fraction = 2)
        @JsonProperty("initial_balance")
        BigDecimal initialBalance
) {
}