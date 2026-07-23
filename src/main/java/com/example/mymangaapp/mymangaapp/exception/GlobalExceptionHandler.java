package com.example.mymangaapp.mymangaapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Hanlde exception tổng quát, ngoài case đã được dự đoán
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ApiResponse<?>> handlingGeneralException(Exception exception) {

        log.error("Lỗi cực kỳ nghiêm trọng sập hệ thống!", exception);

        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_ERROR;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }
    
}
