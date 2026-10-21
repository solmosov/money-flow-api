package com.example.moneyflowapi.category.dto.request;

import com.example.moneyflowapi.common.annotation.SafeText;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to update a category. Fields left null are not changed")
public record UpdateCategoryRequest(
        @Schema(description = "New category name", example = "Food", maxLength = 100, nullable = true)
        @SafeText(max = 100, allowBlank = false, blockFormulaPrefix = true)
        String name,

        @Schema(description = "New icon identifier", example = "Cart", nullable = true)
        @Pattern(regexp = "[a-zA-Z0-9_-]{1,20}")
        @JsonProperty("icon_name")
        String iconName
) {
        public UpdateCategoryRequest {
                name = name == null ? null : name.strip();
        }
}