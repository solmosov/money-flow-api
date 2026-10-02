package com.example.moneyflowapi.category.mapper;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse toDto(Category category) {
        return CategoryResponse.builder()
                .id(category.getId()).name(category.getName()).type(category.getType()).build();
    }

    public Page<CategoryResponse> toPage(Page<Category> categories) {
        return categories.map((this::toDto));
    }
}
