package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.annotation.RateLimit;
import com.example.mymangaapp.mymangaapp.enums.LimitType;
import com.example.mymangaapp.mymangaapp.dto.category.request.CategoryRequest;
import com.example.mymangaapp.mymangaapp.dto.common.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.category.response.CategoryResponse;
import com.example.mymangaapp.mymangaapp.dto.category.response.DeleteCategoryResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;


    // ------------------------------------ endpoints public (cho khách) ------------------------------------ //

    // public để khi vào trang họ có thể biết được có bn thể loại rồi lọc truyện theo đó
    @GetMapping("/categories")
    @RateLimit(capacity = 60)
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAllCategories())
                .build();
    }

    @GetMapping("/categories/{id}")
    @RateLimit(capacity = 60)
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable @NonNull String id) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategoryById(id))
                .build();
    }


    // ------------------------------------ endpoints của admin ------------------------------------ //

    @PostMapping("/admin/categories")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @PatchMapping("/admin/categories/{id}")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<CategoryResponse> updateCategoryById(
            @PathVariable @NonNull String id,
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategoryById(id, request))
                .build();
    }

    @DeleteMapping("/admin/categories/{id}")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<DeleteCategoryResponse> deleteCategoryById(@PathVariable @NonNull String id) {
        categoryService.deleteCategoryById(id);

        return ApiResponse.<DeleteCategoryResponse>builder()
                .message(ResponseCode.SUCCESS.getMessage())
                .result(DeleteCategoryResponse.builder().categoryId(id).build())
                .build();
    }
}
