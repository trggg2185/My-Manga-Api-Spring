package com.example.mymangaapp.mymangaapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ResponseCode {

    /*
        Phân chia các loại mã code theo từng nhóm sau:
            0000 - Success
            1000 - Authentication
            2000 - Authorization
            3000 - Validation
            4000 - Resource
            5000 - Business
            9000 - Internal
    */

    UNCATEGORIZED_ERROR("9000", "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR),
    ENUM_KEY_INVALID("9001", "Invalid enum key!", HttpStatus.INTERNAL_SERVER_ERROR),

    SUCCESS("0000", "Success!", HttpStatus.OK),

    UNAUTHENTICATED("1000", "Unauthenticated!", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED("2000", "Unauthorized!", HttpStatus.FORBIDDEN),

    USERNAME_INVALID("3000", "Tên người dùng không được dưới {min} ký tự!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("3001", "Mật khẩu không được dưới {min} ký tự!", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("3002", "Email không đúng định dạng!", HttpStatus.BAD_REQUEST);
    
    String code;
    String message;
    HttpStatusCode httpStatusCode;

}
