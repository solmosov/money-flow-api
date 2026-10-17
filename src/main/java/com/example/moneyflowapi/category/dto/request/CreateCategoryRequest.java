package com.example.moneyflowapi.category.dto.request;

import com.example.moneyflowapi.category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        CategoryType type,

        @NotBlank
        @Size(max = 20)
        String iconName
) {
}
