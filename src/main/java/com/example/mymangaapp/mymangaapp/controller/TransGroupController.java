package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.annotation.RateLimit;
import com.example.mymangaapp.mymangaapp.enums.LimitType;
import com.example.mymangaapp.mymangaapp.dto.request.TransGroupCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.dto.response.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.TransGroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransGroupController {

    TransGroupService transGroupService;


    // ----------------------------------- endpoint public (cho khách) -----------------------------------//

    // Endpoint này dành cho user thông thường có thể thấy tất cả nhóm dịch đang hoạt động
    @RateLimit(capacity = 4, resetTimeInSeconds = 20)
    @GetMapping("/transgroups")
    public ApiResponse<PaginatedResponse<TransGroupResponse>> getGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        PaginatedResponse<TransGroupResponse> responses = transGroupService.getGroups(page, size, sortBy);

        return ApiResponse.<PaginatedResponse<TransGroupResponse>>builder()
                .result(responses)
                .build();
    }


    // -------------------------------- endpoint cho user đã đăng nhập -----------------------------------//

    @PostMapping("/transgroups")
    @RateLimit(capacity = 3, resetTimeInSeconds = 3600, limitType = LimitType.USER_ID)
    public ApiResponse<TransGroupResponse> requestCreateGroup(@Valid @RequestBody TransGroupCreationRequest request) {
        TransGroupResponse response = transGroupService.requestCreateGroup(request);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }


    // ----------------------------------- endpoint cho admin -----------------------------------//

    @PatchMapping("/admin/transgroups/{id}/approve")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<TransGroupResponse> approveCreateGroup(@PathVariable @NonNull String id) {
        TransGroupResponse response = transGroupService.approveCreateGroup(id);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }

    @PatchMapping("/admin/transgroups/{id}/reject")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<TransGroupResponse> rejectCreateGroup(@PathVariable @NonNull String id) {
        TransGroupResponse response = transGroupService.rejectCreateGroup(id);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/admin/transgroups")
    @RateLimit(capacity = 30, limitType = LimitType.USER_ID)
    public ApiResponse<PaginatedResponse<TransGroupResponse>> getGroups(
            @RequestParam(required = false) TransGroupStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        PaginatedResponse<TransGroupResponse> responses = transGroupService.getGroups(status, page, size, sortBy);

        return ApiResponse.<PaginatedResponse<TransGroupResponse>>builder()
                .result(responses)
                .build();
    }


    // ----------------------------------- endpoint cho leaer và admin -----------------------------//

    @DeleteMapping("/transgroups/{id}")
    @RateLimit(limitType = LimitType.USER_ID)
    public ApiResponse<String> softDeleteGroupById(@PathVariable @NonNull String id) {
        transGroupService.softDeleteGroupById(id);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("Group id: " + id)
                .build();
    }

}
