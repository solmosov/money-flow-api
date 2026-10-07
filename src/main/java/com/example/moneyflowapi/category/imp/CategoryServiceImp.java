package com.example.moneyflowapi.category.imp;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.category.dto.request.CreateCategoryRequest;
import com.example.moneyflowapi.category.mapper.CategoryMapper;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.user.UserRepository;
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
    private final UserRepository userRepo;

    @Override
    public Page<CategoryResponse> findAllByOwner(UUID userId, CategoryType type, Pageable pageable) {
        return mapper.toPage(categoryRepository.findAllByUserId(userId, type, pageable));
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        var saved =  categoryRepository.save(Category.builder()
                .user(userRepo.getReferenceById(userId))
                .name(request.name())
                .type(request.type())
                .iconName(request.iconName())
                .build());

        return mapper.toDto(saved);
    }
}
