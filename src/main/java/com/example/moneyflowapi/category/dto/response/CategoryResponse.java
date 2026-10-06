package com.example.moneyflowapi.category.dto.response;

import com.example.moneyflowapi.category.CategoryType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryResponse(
        UUID id,
        String name,
        CategoryType type,
        String iconName
) {
}
