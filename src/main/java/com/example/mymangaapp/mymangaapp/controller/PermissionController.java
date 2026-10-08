package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.annotation.RateLimit;
import com.example.mymangaapp.mymangaapp.enums.LimitType;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mymangaapp.mymangaapp.dto.permission.request.PermissionRequest;
import com.example.mymangaapp.mymangaapp.dto.common.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.permission.response.PermissionResponse;
import com.example.mymangaapp.mymangaapp.dto.permission.response.DeletePermissionResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;


    // -------------------------- chức năng của admin -------------------------- //

    @PostMapping
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {

        PermissionResponse response = permissionService.createPermission(request);

        return ApiResponse.<PermissionResponse>builder()
                .result(response)
                .build();

    }

    @GetMapping
    @RateLimit(capacity = 30, limitType = LimitType.USER_ID)
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {

        List<PermissionResponse> responses = permissionService.getAllPermissions();

        return ApiResponse.<List<PermissionResponse>>builder()
                .result(responses)
                .build();

    }

    @DeleteMapping("/{id}")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<DeletePermissionResponse> deletePermissionById(@PathVariable @NonNull String id) {

        permissionService.deletePermissionById(id);

        return ApiResponse.<DeletePermissionResponse>builder()
                .message(ResponseCode.SUCCESS.getMessage())
                .result(DeletePermissionResponse.builder().permissionId(id).build())
                .build();
    }

}
