package com.example.moneyflowapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized", HttpStatus.UNAUTHORIZED),
    TRANS_NOT_FOUND("TRANS_NOT_FOUND", "Transaction not found", HttpStatus.NOT_FOUND),
    TRANSACTION_CATEGORY_INVALID("TRANSACTION_CATEGORY_INVALID", "Transaction category invalid", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found. Please try again", HttpStatus.NOT_FOUND),
    USER_BALANCE_INSUFFICIENT("USER_BALANCE_INSUFFICIENT", "User balance insufficient", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "Category not found", HttpStatus.NOT_FOUND),
    TRANSACTION_DAILY_TOTAL_RANGE_LIMIT("TRANSACTION_DAILY_TOTAL_RANGE_LIMIT", "Date range must not exceed  %s days", HttpStatus.BAD_REQUEST),
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
