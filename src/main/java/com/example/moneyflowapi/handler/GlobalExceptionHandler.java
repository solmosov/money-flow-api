package com.example.moneyflowapi.handler;

import com.example.moneyflowapi.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handler(BusinessException ex) {

        ErrorResponse body = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ex.getErrorCode().getCode())
                .build();

        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handler(MethodArgumentNotValidException ex) {

        List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.ValidationError.builder()
                        .field(toSnakeCase(error.getField()))
                        .message(error.getDefaultMessage())
                        .build()
                )
                .toList();

        ErrorResponse body = ErrorResponse.builder()
                .message("Validation failed")
                .code("VALIDATION_ERROR")
                .validationErrors(errors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    private String toSnakeCase(String field) {
        return field.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handler(AuthorizationDeniedException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.name())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handler(BadCredentialsException ex){
        ErrorResponse body = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.name())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handler(Exception ex) {
        log.debug(ex.getMessage(), ex);

        ErrorResponse body = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}
