package com.example.moneyflowapi.category.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(max = 100)
        @Pattern(regexp = ".*\\S.*")
        String name,

        @Size(max = 20)
        @Pattern(regexp = ".*\\S.*")
        String iconName
) {
}
