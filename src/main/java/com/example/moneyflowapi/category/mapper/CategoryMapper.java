package com.example.moneyflowapi.category.mapper;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toDto(Category category) {
        return CategoryResponse.builder()
                .id(category.getId()).name(category.getName()).type(category.getType()).build();
    }
}
