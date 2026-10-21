package com.example.moneyflowapi.goal.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateGoalRequest(
        @NotBlank @Size(max = 80) String name,
        @NotNull @Positive BigDecimal targetAmount,
        @NotNull @Future Instant deadline
) {
}