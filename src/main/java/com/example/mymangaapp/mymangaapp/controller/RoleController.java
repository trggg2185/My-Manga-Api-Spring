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

import com.example.mymangaapp.mymangaapp.dto.role.request.RoleRequest;
import com.example.mymangaapp.mymangaapp.dto.common.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.role.response.RoleResponse;
import com.example.mymangaapp.mymangaapp.dto.role.response.DeleteRoleResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;


    // ----------------------- endpoints cho admin ---------------------------- //

    @PostMapping
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {

        RoleResponse response = roleService.createRole(request);

        return ApiResponse.<RoleResponse>builder()
                .result(response)
                .build();

    }

    @GetMapping
    @RateLimit(capacity = 30, limitType = LimitType.USER_ID)
    public ApiResponse<List<RoleResponse>> getAllRoles() {

        List<RoleResponse> responses = roleService.getAllRoles();

        return ApiResponse.<List<RoleResponse>>builder()
                .result(responses)
                .build();

    }

    @DeleteMapping("/{id}")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<DeleteRoleResponse> deleteRoleById(@PathVariable @NonNull String id) {

        roleService.deleteRoleById(id);

        return ApiResponse.<DeleteRoleResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result(DeleteRoleResponse.builder().roleId(id).build())
                .build();
    }

}
