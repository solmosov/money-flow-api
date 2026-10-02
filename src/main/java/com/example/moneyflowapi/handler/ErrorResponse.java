package com.example.moneyflowapi.handler;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ErrorResponse {

    private String message;
    private String code;
    private List<ValidationError> validationErrors;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class ValidationError {
        private String field;
        private String message;
    }
}
