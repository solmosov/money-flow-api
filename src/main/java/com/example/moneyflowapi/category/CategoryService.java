package com.example.moneyflowapi.category;

import com.example.moneyflowapi.category.dto.request.CategoryRequest;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.category.dto.response.CreateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    Page<CategoryResponse> findAllByOwner(UUID userId, CategoryType type, Pageable pageable);
    CategoryResponse create(CreateCategoryRequest request);
}
