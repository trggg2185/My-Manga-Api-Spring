package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.TransGroupCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.MangaService;
import com.example.mymangaapp.mymangaapp.service.TransGroupService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransGroupController {

    TransGroupService transGroupService;

    @PostMapping("/transgroups")
    public ApiResponse<TransGroupResponse> requestCreateGroup(@RequestBody TransGroupCreationRequest request) {
        TransGroupResponse response = transGroupService.requestCreateGroup(request);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }

    // Endpoint này dành cho user thông thường có thể thấy tất cả nhóm dịch đang hoạt động
    @GetMapping("/transgroups")
    public ApiResponse<List<TransGroupResponse>> getGroups() {
        List<TransGroupResponse> responses = transGroupService.getGroups();

        return ApiResponse.<List<TransGroupResponse>>builder()
                .result(responses)
                .build();
    }

    @DeleteMapping("/transgroups/{id}")
    public ApiResponse<String> softDeleteGroupById(@PathVariable @NonNull String id) {
        transGroupService.softDeleteGroupById(id);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("Group id: " + id)
                .build();
    }

    @GetMapping("/admin/transgroups")
    public ApiResponse<List<TransGroupResponse>> getGroups(
            @RequestParam(required = false) TransGroupStatus status
    ) {
        List<TransGroupResponse> responses = transGroupService.getGroups(status);

        return ApiResponse.<List<TransGroupResponse>>builder()
                .result(responses)
                .build();
    }

    @PatchMapping("/admin/transgroups/{id}/approve")
    public ApiResponse<TransGroupResponse> approveCreateGroup(@PathVariable @NonNull String id) {
        TransGroupResponse response = transGroupService.approveCreateGroup(id);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }

    @PatchMapping("/admin/transgroups/{id}/reject")
    public ApiResponse<TransGroupResponse> rejectCreateGroup(@PathVariable @NonNull String id) {
        TransGroupResponse response = transGroupService.rejectCreateGroup(id);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }

}
