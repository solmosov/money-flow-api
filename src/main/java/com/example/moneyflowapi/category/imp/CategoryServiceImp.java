package com.example.moneyflowapi.category.imp;

import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.category.dto.request.CategoryRequest;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements com.example.moneyflowapi.category.CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Override
    public Page<CategoryResponse> findAllByOwner(UUID userId, CategoryType type, Pageable pageable) {
        return mapper.toPage(categoryRepository.findAllByUserId(userId, type, pageable));
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        return null;
    }
}
