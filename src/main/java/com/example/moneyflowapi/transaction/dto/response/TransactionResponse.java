package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.transaction.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record TransactionResponse(
        UUID id,
        TransactionType type,
        CategoryResponse category,
        BigDecimal amount,
        Instant date,
        String description,

        Instant createdAt,
        Instant updatedAt
) {
}
