package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

// Đây là 1 generic chuẩn hoá lại việc response trả về khi paginate
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaginatedResponse<T> {

    // Các fields cần thiết cho frontend
    int currentPage; // số trang hiện tại
    int pageSize; // số bản ghi mỗi trang
    int totalPages; // tổng số trang
    long totalElements; // tổng số bản ghi
    List<T> content; // dữ liệu chính

    // hàm tiện giúp convert từ page sang paginated response nhanh
    public static <T> PaginatedResponse<T> of(Page<T> page) {
        return PaginatedResponse.<T>builder()
                .currentPage(page.getNumber() + 1) // trang bắt đầu từ 0, nhưng frontend làm từ 1
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(page.getContent())
                .build();
    }

}
