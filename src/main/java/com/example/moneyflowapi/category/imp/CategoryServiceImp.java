package com.example.moneyflowapi.category.imp;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryService;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.category.dto.request.CreateCategoryRequest;
import com.example.moneyflowapi.category.dto.request.UpdateCategoryRequest;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.category.mapper.CategoryMapper;
import com.example.moneyflowapi.category.resolver.DefaultCategoryResolver;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final UserRepository userRepo;
    private final DefaultCategoryResolver defaultCategoryResolver;

    @Override
    @Transactional
    public void initializeForNewUser(UUID userId, String language) {
        User user = userRepo.getReferenceById(userId);
        Instant now = Instant.now();

        List<Category> categories = defaultCategoryResolver.resolve(language).stream()
                .map(item -> Category.builder()
                        .user(user)
                        .name(item.name())
                        .type(item.type())
                        .iconName(item.iconName())
                        .usedAt(now)
                        .build())
                .toList();

        categoryRepository.saveAll(categories);
    }

    @Override
    public Page<CategoryResponse> findAllByOwner(UUID userId, CategoryType type, Pageable pageable) {
        return mapper.toPage(categoryRepository.findAllByUserId(userId, type, pageable));
    }

    @Override
    public CategoryResponse findOne(UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Category category = categoryRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        return mapper.toDto(category);
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        var saved = categoryRepository.save(Category.builder()
                .user(userRepo.getReferenceById(userId))
                .name(request.name())
                .type(request.type())
                .iconName(request.iconName())
                .usedAt(Instant.now())
                .build());

        return mapper.toDto(saved);
    }

    @Override
    public CategoryResponse update(UUID categoryId, UpdateCategoryRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Category category = categoryRepository.findByIdAndUserIdAndDeletedAtIsNull(categoryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        if (request.name() != null) {
            category.setName(request.name());
        }

        if (request.iconName() != null) {
            category.setIconName(request.iconName());
        }

        category.setUsedAt(Instant.now());

        categoryRepository.save(category);
        return mapper.toDto(category);
    }

    @Override
    @Transactional
    public void delete(UUID categoryId) {
        UUID userId = SecurityUtil.getCurrentUserId();
        int deleted = categoryRepository.softDeleteByIdAndUserId(categoryId, userId, Instant.now());

        if (deleted == 0) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }
}
