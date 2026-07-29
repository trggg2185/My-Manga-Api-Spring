package com.example.mymangaapp.mymangaapp.controller;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mymangaapp.mymangaapp.dto.request.PermissionRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PermissionResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {

        PermissionResponse response = permissionService.createPermission(request);

        return ApiResponse.<PermissionResponse>builder()
                .result(response)
                .build();

    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {

        List<PermissionResponse> responses = permissionService.getAllPermissions();

        return ApiResponse.<List<PermissionResponse>>builder()
                .result(responses)
                .build();

    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePermissionById(@PathVariable @NonNull String id) {

        permissionService.deletePermissionById(id);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("Permission id: " + id)
                .build();
    }

}
