package com.example.moneyflowapi.userAccount.dto.request;

import com.example.moneyflowapi.currency.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserStartRequest(
        @NotNull
        CurrencyCode currencyCode
) {
}
