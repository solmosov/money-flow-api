package com.example.moneyflowapi.category.dto.request;

import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.common.annotation.SafeText;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to create a category")
public record CreateCategoryRequest(
        @Schema(description = "Category name", example = "Groceries", maxLength = 100)
        @NotBlank
        @SafeText(max = 100, allowBlank = false, blockFormulaPrefix = true)
        String name,

        @Schema(description = "Category type")
        @NotNull
        CategoryType type,

        @Schema(description = "Icon identifier", example = "ShoppingCart")
        @NotBlank
        @Pattern(regexp = "[a-zA-Z0-9_-]{1,20}")
        @JsonProperty("icon_name")
        String iconName
) {
        public CreateCategoryRequest {
                name = name == null ? null : name.strip();
        }
}