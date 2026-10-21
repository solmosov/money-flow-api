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
    VERIFICATION_CODE_TOO_FREQUENT("VERIFICATION_CODE_TOO_FREQUENT", "Verification code too frequent", HttpStatus.BAD_REQUEST),
    TELEGRAM_NOT_LINKED("TELEGRAM_NOT_LINKED", "Telegram not linked", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_EXPIRED("VERIFICATION_CODE_EXPIRED", "Verification code expired", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_ATTEMPTS_EXCEEDED("VERIFICATION_CODE_ATTEMPTS_EXCEEDED", "Verification code attempts exceeded", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_INVALID("VERIFICATION_CODE_INVALID","Verification code invalid", HttpStatus.BAD_REQUEST),
    EXPORTS_INVALID_OR_EXPIRED_LINK("EXPORTS_INVALID_OR_EXPIRED_LINK", "Invalid or expired link", HttpStatus.FORBIDDEN),
    EXPORTS_TOO_MANY_REQUESTS("EXPORTS_TOO_MANY_REQUESTS", "Export queue is full", HttpStatus.TOO_MANY_REQUESTS),
    EXPORTS_INVALID_DATE_RANGE("EXPORTS_INVALID_DATE_RANGE", "Invalid date range", HttpStatus.BAD_REQUEST),
    EXPORTS_NOT_FOUND("EXPORTS_NOT_FOUND", "Export not found", HttpStatus.NOT_FOUND),
    EXPORTS_NOT_AVAILABLE("EXPORTS_NOT_AVAILABLE", "Export is not available", HttpStatus.NOT_FOUND),
    EXPORTS_ALREADY_HAVE_ACTIVE_EXPORT("EXPORTS_ALREADY_HAVE_ACTIVE_EXPORT", "You already have an active export", HttpStatus.TOO_MANY_REQUESTS),
    EXPORTS_FILE_MISSING("EXPORTS_FILE_MISSING", "Export file is missing", HttpStatus.NOT_FOUND),
    EXPORTS_FILE_UNREADABLE("EXPORTS_FILE_UNREADABLE", "Export file is empty or unreadable", HttpStatus.INTERNAL_SERVER_ERROR),
    EXPORTS_FILE_PERMISSION_DENIED("EXPORTS_FILE_PERMISSION_DENIED", "Export file is not accessible by the web server", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND", "Your account not found", HttpStatus.NOT_FOUND),
    CURRENCY_NOT_FOUND("CURRENCY_NOT_FOUND", "Currency not found", HttpStatus.NOT_FOUND),
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
