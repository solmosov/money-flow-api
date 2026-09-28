package com.example.moneyflowapi.category.dto.response;

import com.example.moneyflowapi.category.CategoryType;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        CategoryType type
) {
}
