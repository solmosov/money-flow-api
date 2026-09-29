package com.example.moneyflowapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized", HttpStatus.UNAUTHORIZED),
    TRANS_NOT_FOUND("TRANS_NOT_FOUND", "Transaction not found", HttpStatus.NOT_FOUND),
    TRANSACTION_CATEGORY_INVALID("TRANSACTION_CATEGORY_INVALID", "Transaction category invalid", HttpStatus.NOT_FOUND),
    ;

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
