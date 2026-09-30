package com.example.moneyflowapi.common.pageable;

import com.example.moneyflowapi.common.response.PageResponse;
import org.springframework.data.domain.Page;

public final class PageResponseMapper {
    private PageResponseMapper() {
    }

    public static <T> PageResponse<T> map(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
