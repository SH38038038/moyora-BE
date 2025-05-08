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

    // 다른 예외들도 추가로 처리 가능
}
