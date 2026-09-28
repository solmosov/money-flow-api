package com.example.moneyflowapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(
            String code,
            String defaultMessage,
            HttpStatus status
    ) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
