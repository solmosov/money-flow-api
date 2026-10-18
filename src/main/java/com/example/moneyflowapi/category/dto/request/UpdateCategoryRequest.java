package com.example.moneyflowapi.category.dto.request;

import com.example.moneyflowapi.common.annotation.SafeText;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @SafeText(max = 100, allowBlank = false, blockFormulaPrefix = true)
        String name,

        @Pattern(regexp = "[a-zA-Z0-9_-]{1,20}")
        String iconName
) {
        public UpdateCategoryRequest {
                name = name == null ? null : name.strip();
        }
}