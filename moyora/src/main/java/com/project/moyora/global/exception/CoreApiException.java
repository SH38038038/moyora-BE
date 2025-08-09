package com.project.moyora.global.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CoreApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public CoreApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}