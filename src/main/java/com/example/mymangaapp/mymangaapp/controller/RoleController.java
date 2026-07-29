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

import com.example.mymangaapp.mymangaapp.dto.request.RoleRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.RoleResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {

        RoleResponse response = roleService.createRole(request);

        return ApiResponse.<RoleResponse>builder()
                .result(response)
                .build();

    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {

        List<RoleResponse> responses = roleService.getAllRoles();

        return ApiResponse.<List<RoleResponse>>builder()
                .result(responses)
                .build();

    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRoleById(@PathVariable @NonNull String id) {

        roleService.deleteRoleById(id);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("Role id: " + id)
                .build();
    }

}
