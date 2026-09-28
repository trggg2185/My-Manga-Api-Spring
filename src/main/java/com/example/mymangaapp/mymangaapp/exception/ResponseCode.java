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
            6000 -
            9000 - Internal
    */

    UNCATEGORIZED_ERROR("9001", "Lỗi không xác định!", HttpStatus.INTERNAL_SERVER_ERROR),
    ENUM_KEY_INVALID("9002", "Enum key không hợp lệ!", HttpStatus.INTERNAL_SERVER_ERROR),
    RUNTIME_EXCEPTION("9003", "Lỗi ngoại lệ runtime!", HttpStatus.INTERNAL_SERVER_ERROR),
    URL_INVALID("9004", "Lỗi url không hợp lệ!", HttpStatus.INTERNAL_SERVER_ERROR),
    HTTP_MESSAGE_NOT_READABLE("9005", "Lỗi không thể đọc message HTTP!", HttpStatus.INTERNAL_SERVER_ERROR),
    STORAGE_SERVICE_UNAVAILABLE("9006", "Dịch vụ lưu trũ không khả dụng!", HttpStatus.SERVICE_UNAVAILABLE),
    STORAGE_SERVICE_ERROR("9007", "Lỗi khi tương tác với dịch vụ lưu trũ!", HttpStatus.INTERNAL_SERVER_ERROR),

    SUCCESS("0000", "Thành công!", HttpStatus.OK),

    UNAUTHENTICATED("1001", "Chưa được xác thực!", HttpStatus.UNAUTHORIZED),
    PASSWORD_INCORRECT("1002", "Mật khẩu không chính xác!", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED("2001", "Không có quyền!", HttpStatus.FORBIDDEN),

    USERNAME_INVALID("3001", "Tên người dùng phải có ít nhất {min} ký tự và tối đa {max} Ký tự!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("3002", "Mật khẩu phải có ít nhất {min} ký tự và tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("3003", "Email không đúng định dạng!", HttpStatus.BAD_REQUEST),
    BIO_INVALID("3004", "Thông tin cá nhân có tối đa {max} Ký tự!", HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED("3005", "Tên người dùng không được để trống!", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("3006", "Mật khẩu không được để trống!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_NAME_INVALID("3007", "Tên nhóm dịch ít nhất phải có {min} ký tự và tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    MANGA_NAME_INVALID("3008", "Tên truyện có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    AUTHORS_NAME_INVALID("3009", "Tên tác giả có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    GENRES_REQUIRED("3010", "Thể loại truyện không được để trống!", HttpStatus.BAD_REQUEST),
    FILE_REQUIRED("3011", "File không được để trống!", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_REQUIRED("3012", "Tên thể loại không được để trống!", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_INVALID("3013", "Tên thể loại phải có ít nhất {min} ký tự và tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    CATEGORIES_REQUIRED("3014", "Danh sách thể loại không được để trống!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_NAME_REQUIRED("3015", "Tên nhóm dịch không được để trống!", HttpStatus.BAD_REQUEST),
    ROLE_NAME_REQUIRED("3016", "Tên vai trò không được để trống!", HttpStatus.BAD_REQUEST),
    ROLE_NAME_INVALID("3017", "Tên vai trò có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    ROLE_DESCRIPTION_INVALID("3018", "Mô tả vai trò có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    PERMISSIONS_REQUIRED("3019", "Danh sách quyền không được để trống!", HttpStatus.BAD_REQUEST),
    MANGA_STATUS_REQUIRED("3020", "Trạng thái truyện không được để trống!", HttpStatus.BAD_REQUEST),
    MANGA_DESCRIPTION_INVALID("3021", "Mô tả truyện có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    TOKEN_REQUIRED("3022", "Chuỗi token không được để trống!", HttpStatus.BAD_REQUEST),
    CHAPTER_INDEX_REQUIRED("3023", "Số chương không được để trống!", HttpStatus.BAD_REQUEST),
    CHAPTER_INDEX_POSITIVE("3024", "Số chương phải là số nguyên dương!", HttpStatus.BAD_REQUEST),
    CHAPTER_TITLE_INVALID("3025", "Tiêu đề chương có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    PAGE_URLS_REQUIRED("3026", "Danh sách url của ảnh không được để trống!", HttpStatus.BAD_REQUEST),
    PERMISSION_NAME_REQUIRED("3027", "Tên quyền không được để trống!", HttpStatus.BAD_REQUEST),
    PERMISSION_NAME_INVALID("3028", "Tên quyền có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    PERMISSION_DESCRIPTION_INVALID("3029", "Mô tả quyền có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),
    FILE_SIZE_INVALID("3030", "Kích thước file quá lớn!", HttpStatus.BAD_REQUEST),
    PERMISSION_INVALID("3031", "Các quyền không hợp lệ! (Các quyền không tồn tại hoặc mảng các quyền của request rỗng)", HttpStatus.BAD_REQUEST),
    PERMISSION_REQUIRED("3032", "Mảng các quyền không được để trống!", HttpStatus.BAD_REQUEST),
    FIELD_VALUE_INVALID("3033", "Giá trị trường không hợp lệ!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_DESCRIPTION_INVALID("3034", "Mô tả nhóm dịch có tối đa {max} ký tự!", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND("4001", "Người dùng không tồn tại!", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("4002", "Vai trò không tồn tại!", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND("4003", "Quyền không tồn tại!", HttpStatus.NOT_FOUND),
    TRANSGROUP_NOT_FOUND("4004", "Nhóm dịch không tồn tại!", HttpStatus.NOT_FOUND),
    TRANSGROUP_JOIN_REQUEST_NOT_FOUND("4005", "Yêu cầu vào nhóm không tồn tại!", HttpStatus.NOT_FOUND),
    MANGA_NOT_FOUND("4006", "Manga không tồn tại!", HttpStatus.NOT_FOUND),
    CHAPTER_NOT_FOUND("4007", "Chương này không tồn tại!", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("4008", "Thể loại không tồn tại!", HttpStatus.NOT_FOUND),
    TRANSGROUP_CREATION_REQUEST_NOT_FOUND("4009", "Yêu cầu tạo nhóm dịch không tồn tại!", HttpStatus.NOT_FOUND),

    USERNAME_ALREADY_EXISTS("5001", "Tên người dùng đã tồn tại!", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("5002", "Email đã tồn tại!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_NAME_ALREADY_EXISTS("5003", "Tên nhóm dịch đã tồn tại!", HttpStatus.BAD_REQUEST),
    ROLE_NAME_ALREADY_EXISTS("5004", "Tên vai trò đã tồn tại!", HttpStatus.BAD_REQUEST),
    PERMISSION_NAME_ALREADY_EXISTS("5005", "Tên quyền đã tồn tại!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_STATUS_INVALID("5006", "Nhóm dịch đã được chấp nhận, bị từ chối hoặc đã bị xoá!", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_GROUP("5007", "Người dùng đã trong nhóm dịch!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_NOT_APPROVED("5008", "Nhóm dịch chưa được duyệt!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_JOIN_REQUEST_STATUS_INVALID("5009", "Yêu cầu vào nhóm đã được chập nhận hoặc bị tự chối!", HttpStatus.BAD_REQUEST),
    FILE_INVALID("5010", "File không hợp lệ!", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED("5011", "Upload file thất bại!", HttpStatus.BAD_REQUEST),
    FILE_COPY_FAILED("5012", "Copy file thất bại!", HttpStatus.BAD_REQUEST),
    CHAPTER_INDEX_ALREADY_EXISTS("5013", "Chapter index đã tồn tại!", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_ALREADY_EXISTS("5014", "Tên thể loại đã tồn tại!", HttpStatus.BAD_REQUEST),
    DELETE_FILE_FAILED("5015", "Xoá file thất bại!", HttpStatus.BAD_REQUEST),
    DATA_INTEGRITY_VIOLATION("5016", "Dữ liệu vi phạm ràng buộc!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_JOIN_REQUEST_ALREADY_EXISTS("5017", "Yêu cầu vào nhóm dịch đã tồn tại!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_CREATION_REQUEST_ALREADY_EXISTS("5018", "Yêu cầu tạo nhóm dịch đã tồn tại!", HttpStatus.BAD_REQUEST),
    TRANSGROUP_CREATION_REQUEST_STATUS_INVALID("5019", "Yêu cầu tạo nhóm dịch đã được chấp nhận hoặc bị từ chối!", HttpStatus.BAD_REQUEST),

    RATE_LIMIT_EXCEEDED("6001", "Đạt giới hạn thao tác, vui lòng thử lại sau!", HttpStatus.TOO_MANY_REQUESTS),
    METHOD_NOT_ALLOWED("6002", "Phương thức không được phép!", HttpStatus.METHOD_NOT_ALLOWED);

    String code;
    String message;
    HttpStatusCode httpStatusCode;

}
