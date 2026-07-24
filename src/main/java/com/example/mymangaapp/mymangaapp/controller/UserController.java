package com.example.mymangaapp.mymangaapp.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mymangaapp.mymangaapp.dto.request.UserCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.UserResponse;
import com.example.mymangaapp.mymangaapp.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    
    @PostMapping
    // Nhớ có annotation @Valid để validate các fields trong request
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {

        UserResponse response = userService.createUser(request);

        return ApiResponse.<UserResponse>builder()
                .result(response)
                .build();

    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {

        List<UserResponse> responses = userService.getAllUsers();

        return ApiResponse.<List<UserResponse>>builder()
                .result(responses)
                .build();

    }

}
