package com.example.moneyflowapi.category;

import com.example.moneyflowapi.category.dto.request.UpdateCategoryRequest;
import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.category.dto.request.CreateCategoryRequest;
import com.example.moneyflowapi.common.pageable.PageResponseMapper;
import com.example.moneyflowapi.common.response.PageResponse;
import com.example.moneyflowapi.security.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name="Category")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> findAll(
            @RequestParam(required = false) CategoryType type,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        Page<CategoryResponse> page = categoryService.findAllByOwner(
                SecurityUtil.getCurrentUserId(),
                type,
                pageable
        );

        return ResponseEntity.ok(PageResponseMapper.map(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findOne(
            @PathVariable("id") UUID id
    ){
        return ResponseEntity.ok(categoryService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updater(
            @PathVariable("id") UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.update(categoryId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID categoryId
    ) {
        categoryService.delete(categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
