package com.example.moneyflowapi.category.dto.request;

import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.common.annotation.SafeText;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank
        @SafeText(max = 100, allowBlank = false, blockFormulaPrefix = true)
        String name,

        @NotNull
        CategoryType type,

        @NotBlank
        @Pattern(regexp = "[a-z0-9_-]{1,20}")
        String iconName
) {
        public CreateCategoryRequest {
                name = name == null ? null : name.strip();
        }
}