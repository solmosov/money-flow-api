package com.example.moneyflowapi.category.dto.request;

import com.example.moneyflowapi.category.CategoryType;

public record CategoryRequest(
        String name,
        CategoryType type
) {
}
