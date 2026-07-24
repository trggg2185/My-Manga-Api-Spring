package com.example.mymangaapp.mymangaapp.exception;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    // Hanlde exception tổng quát, ngoài case đã được dự đoán
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ApiResponse<?>> handlingGeneralException(Exception exception) {

        log.error("Lỗi cực kỳ nghiêm trọng sập hệ thống!", exception);

        ResponseCode responseCode = ResponseCode.UNCATEGORIZED_ERROR;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .build();

        return ResponseEntity
                .status(responseCode.getHttpStatusCode())
                .body(apiResponse);
    }

    // Handle những exception bắn ra do vi phạm validate ở request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ApiResponse<?>> handlingValidationException(MethodArgumentNotValidException exception) {

        log.error("Giá trị các trường của request không hợp lệ!", exception);

        ResponseCode responseCode = ResponseCode.ENUM_KEY_INVALID;

        FieldError fieldError = exception.getFieldError();

        // Lấy message ở field bị lỗi, chính là enum key của ResponseCode
        String enumKey = fieldError.getDefaultMessage();

        try {
            responseCode = ResponseCode.valueOf(enumKey);
            // Bắt ngoại lệ enum key để tránh việc GlobalExceptionHandler đứng ra giải 
            // quyết và trả về response UNCATEGORIZED_ERROR 500
        } catch (IllegalArgumentException e) {
            log.error("Lỗi enum key không hợp lệ", e);
        }

        // Lấy đối tượng chứa thông tin chi tiết về 1 ràng buộc cụ thể đã vi phạm
        ConstraintViolation<?> constraintViolation = exception
                .getBindingResult()
                .getAllErrors()
                .getFirst()
                .unwrap(ConstraintViolation.class);

        // Đây là mảng thông tin vi phạm
        // ví dụ: nếu vi phạm min thì trong map sẽ có <"min", 3>
        Map<String, Object> attributes = constraintViolation.getConstraintDescriptor().getAttributes();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(responseCode.getCode())
                .message(
                        Objects.nonNull(attributes)
                                ? mapAttribute(responseCode.getMessage(), attributes)
                                : responseCode.getMessage())
                .build();

        return ResponseEntity
                .status(responseCode.getHttpStatusCode())
                .body(apiResponse);
    }

    // Hàm chuyển message của response code thành 1 message cụ thể bằng
    // cách lấy thông tin từ ràng buộc bị vi phạm khi validate
    private String mapAttribute(String message, Map<String, Object> attributes) {
        
        if (Objects.isNull(attributes.get(MIN_ATTRIBUTE))) {
            return message;
        }
        
        // Lấy giá trị của ràng buộc min ra (là số dưới dạng Object) chuyển thành chuỗi
        String minValue = attributes.get(MIN_ATTRIBUTE).toString();

        // Thay thế message từ {min} thành 1 con số lấy từ ràng buộc bị vi phạm
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

}
