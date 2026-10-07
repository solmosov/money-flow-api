package com.example.moneyflowapi.category;

import com.example.moneyflowapi.category.dto.response.CategoryResponse;
import com.example.moneyflowapi.category.dto.request.CreateCategoryRequest;
import com.example.moneyflowapi.common.pageable.PageResponseMapper;
import com.example.moneyflowapi.common.response.PageResponse;
import com.example.moneyflowapi.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @RequestBody CreateCategoryRequest request
    ){
        return ResponseEntity.ok(categoryService.create(request));
    }
}
