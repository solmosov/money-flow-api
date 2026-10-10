package com.example.moneyflowapi.common.response;

import org.springframework.data.domain.Slice;

public final class SliceResponseMapper {

    private SliceResponseMapper() {}

    public static <T> SliceResponse<T> map(Slice<T> slice) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext()
        );
    }
}