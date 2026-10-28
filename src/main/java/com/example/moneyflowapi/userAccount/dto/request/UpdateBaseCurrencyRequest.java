package com.example.moneyflowapi.userAccount.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateBaseCurrencyRequest(
        @NotNull
        @Pattern(regexp = "^[A-Z]{3}$")
        String currencyCode
) {
}
