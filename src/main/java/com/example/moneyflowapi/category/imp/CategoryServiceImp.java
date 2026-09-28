package com.example.moneyflowapi.category.imp;

import com.example.moneyflowapi.category.dto.request.CategoryRequest;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements com.example.moneyflowapi.category.CategoryService {


    @Override
    public List<CategoryResponse> findAll() {
        return List.of();
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        return null;
    }
}
