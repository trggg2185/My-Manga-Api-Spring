package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.UserPasswordRequest;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.dto.response.UserSummaryResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.example.mymangaapp.mymangaapp.dto.request.UserCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.request.UserUpdateRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.UserResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    // Nếu dùng validate ở trong controller như @NotBlank kết hợp với @Valid thì sẽ kích hoạt HandlerMethodValidator
    // ném ra HandlerMethodValidationException chứ không phải MethodArgumentNotValidException
    // nên muốn tắt id null type safety thì dùng @NonNull của spring để tắt warning 
    // chứ không nên dùng @NotBlank của jakarta

    UserService userService;

    @PostMapping("/users")
    // Nhớ có annotation @Valid để validate các fields trong request
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {

        UserResponse response = userService.createUser(request);

        return ApiResponse.<UserResponse>builder()
                .result(response)
                .build();

    }

    @GetMapping("/users/me")
    public ApiResponse<UserSummaryResponse> getMyInfo() {

        UserSummaryResponse response = userService.getMyInfo();

        return ApiResponse.<UserSummaryResponse>builder()
                .result(response)
                .build();

    }

    @PatchMapping("/users/me")
    public ApiResponse<UserSummaryResponse> updateMyInfo(
            @Valid @RequestBody UserUpdateRequest request
    ) {

        UserSummaryResponse response = userService.updateMyInfo(request);

        return ApiResponse.<UserSummaryResponse>builder()
                .result(response)
                .build();

    }

    // update password của user hiện tại
    @PatchMapping("/users/me/password")
    public ApiResponse<Void> updateMyPassword(
            @Valid @RequestBody UserPasswordRequest request
    ) {

        userService.updateMyPassword(request);

        return ApiResponse.<Void>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .build();

    }

    // fix lại chỉ có admin lấy đc user băng id
    @GetMapping("/admin/users/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable @NonNull String id) {

        UserResponse response = userService.getUserById(id);

        return ApiResponse.<UserResponse>builder()
                .result(response)
                .build();

    }

    // fix lại chỉ có adminmới đc update user
    @PatchMapping("/admin/users/{id}")
    public ApiResponse<UserResponse> updateUserById(@PathVariable @NonNull String id,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateUserById(id, request);

        return ApiResponse.<UserResponse>builder()
                .result(response)
                .build();
    }

    @DeleteMapping("/admin/users/{id}")
    public ApiResponse<String> deleteUserById(@PathVariable @NonNull String id) {

        userService.deleteUserById(id);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("User id: " + id)
                .build();
    }

    @GetMapping("/admin/users")
    public ApiResponse<PaginatedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {

        PaginatedResponse<UserResponse> responses = userService.getAllUsers(page, size, sortBy);

        return ApiResponse.<PaginatedResponse<UserResponse>>builder()
                .result(responses)
                .build();

    }

}
