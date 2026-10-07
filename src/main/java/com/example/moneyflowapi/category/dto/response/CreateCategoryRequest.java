package com.example.moneyflowapi.category.dto.response;

import com.example.moneyflowapi.category.CategoryType;

public record CreateCategoryRequest(
        String name,
        CategoryType type,
        String iconName
) {
}
