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

    UNCATEGORIZED_ERROR("9001", "Lỗi không xác định!", HttpStatus.INTERNAL_SERVER_ERROR),
    ENUM_KEY_INVALID("9002", "Enum key không hợp lệ!", HttpStatus.INTERNAL_SERVER_ERROR),
    RUNTIME_EXCEPTION("9003", "Lỗi ngoại lệ runtime!", HttpStatus.INTERNAL_SERVER_ERROR),

    SUCCESS("0000", "Thành công!", HttpStatus.OK),

    UNAUTHENTICATED("1001", "Chưa được xác thực!", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED("2001", "Không có quyền!", HttpStatus.FORBIDDEN),

    USERNAME_INVALID("3001", "Tên người dùng không được dưới {min} ký tự!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("3002", "Mật khẩu không được dưới {min} ký tự!", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("3003", "Email không đúng định dạng!", HttpStatus.BAD_REQUEST),
    BIO_INVALID("3004", "Thông tin cá nhân không quá {max} Ký tự!", HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED("3005", "Tên người dùng không được để trống!", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("3006", "Mật khẩu không được để trống!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_NAME_INVALID("3007", "Tên nhóm dịch ít nhất phải có {min} ký tự!", HttpStatus.BAD_REQUEST),
    MANGA_NAME_INVALID("3008", "Tên truyện ít nhất phải có {min} ký tự!", HttpStatus.BAD_REQUEST),
    AUTHORS_NAME_REQUIRED("3009", "Tên tác giả không được để trống!", HttpStatus.BAD_REQUEST),
    GENRES_REQUIRED("30010", "Thể loại truyện không được để trống!", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND("4001", "Người dùng không tồn tại!", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("4002", "Vai trò không tồn tại!", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND("4003", "Quyền không tồn tại!", HttpStatus.NOT_FOUND),
    TRANSGROUP_NOT_FOUND("4004", "Nhóm dịch không tồn tại!", HttpStatus.NOT_FOUND),

    USERNAME_ALREADY_EXISTS("5001", "Tên người dùng đã tồn tại!", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("5002", "Email đã tồn tại!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_NAME_ALREADY_EXISTS("5003", "Tên nhóm dịch đã tồn tại!", HttpStatus.BAD_REQUEST),
    ROLE_NAME_ALREADY_EXISTS("5004", "Tên vai trò đã tồn tại!", HttpStatus.BAD_REQUEST),
    PERMISSION_NAME_ALREADY_EXISTS("5005", "Tên quyền đã tồn tại!", HttpStatus.BAD_REQUEST),
    PERMISSION_INVALID("5006", "Các quyền không hợp lệ! (Các quyền không tồn tại hoặc mảng các quyền của request rỗng)", HttpStatus.BAD_REQUEST),
    PERMISSION_REQUIRED("5007", "Mảng các quyền không được để trống!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_STATUS_INVALID("5008", "Nhóm dịch đã được chấp nhận, bị từ chối hoặc đã bị xoá!", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_GROUP("5009", "Người dùng đã trong nhóm dịch!", HttpStatus.BAD_REQUEST),
    MANGA_ALREADY_EXISTS("5010", "Truyện đã tồn tại!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_NOT_APPROVED("5011", "Nhóm dịch chưa được duyệt!", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode httpStatusCode;

}
