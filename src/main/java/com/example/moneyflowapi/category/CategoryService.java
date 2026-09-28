package com.example.moneyflowapi.category;

import com.example.moneyflowapi.category.dto.request.CategoryRequest;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> findAll();
    CategoryResponse create(CategoryRequest request);
}
