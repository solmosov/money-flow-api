package com.example.moneyflowapi.goal.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateGoalRequest(
        @NotBlank @Size(max = 80) String name,
        @NotNull @Positive BigDecimal targetAmount,
        @NotBlank @Size(min = 3, max = 3) String currencyCode,
        @NotNull @PositiveOrZero BigDecimal startingAmount,
        @NotNull @Future Instant deadline
) {
}