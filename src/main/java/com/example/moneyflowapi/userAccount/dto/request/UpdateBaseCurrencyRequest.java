package com.example.moneyflowapi.userAccount.dto.request;

import com.example.moneyflowapi.currency.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateBaseCurrencyRequest(
        @NotNull
        @Pattern(regexp = "^[A-Z]{3}$")
        CurrencyCode currencyCode
) {
}
