package com.project.moyora.global.exception;

import com.project.moyora.global.exception.model.ApiResponseTemplete;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleAccessDeniedException(AccessDeniedException ex) {
        return ApiResponseTemplete.error(ErrorCode.FORBIDDEN_AUTH_EXCEPTION, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleNotFound(ResourceNotFoundException ex) {
        return ApiResponseTemplete.error(ErrorCode.NOTICE_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseTemplete<String>> handleGeneral(Exception ex) {
        return ApiResponseTemplete.error(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
