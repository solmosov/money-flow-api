package com.example.moneyflowapi.wallet.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateRateAmountRequest(

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 10)
        BigDecimal rateAmount
) {
}
