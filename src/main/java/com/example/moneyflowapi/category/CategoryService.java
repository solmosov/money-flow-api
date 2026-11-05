package com.example.moneyflowapi.category;

import com.example.moneyflowapi.category.dto.request.UpdateCategoryRequest;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.category.dto.request.CreateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    void initializeForNewUser(UUID userId, String language);
    Page<CategoryResponse> findAllByOwner(UUID userId, CategoryType type, Pageable pageable);
    CategoryResponse findOne(UUID id);
    CategoryResponse create(CreateCategoryRequest request);
    CategoryResponse update(UUID categoryId, UpdateCategoryRequest request);
    void delete(UUID categoryId);
}
