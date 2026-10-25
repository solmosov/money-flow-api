package com.example.moneyflowapi.goal.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Request to create a savings goal")
public record CreateGoalRequest(
        @Schema(description = "Goal name", example = "New laptop", maxLength = 80)
        @NotBlank
        @Size(max = 80)
        String name,

        @Schema(description = "Amount to reach", example = "12000000.00")
        @NotNull
        @Positive
        @JsonProperty("target_amount")
        BigDecimal targetAmount,

        @Schema(description = "ISO 4217 currency code", example = "UZS")
        @NotBlank
        @Size(min = 3, max = 3)
        @JsonProperty("currency_code")
        String currencyCode,

        @NonNull
        @Positive
        @Digits(integer = 8, fraction = 2)
        BigDecimal rateAmount,

        @Schema(description = "Amount already saved", example = "0.00")
        @NotNull
        @PositiveOrZero
        @JsonProperty("starting_amount")
        BigDecimal startingAmount,

        @Schema(description = "Goal deadline (must be in the future)", example = "2027-03-01T00:00:00Z")
        @NotNull
        @Future
        Instant deadline
) {
}