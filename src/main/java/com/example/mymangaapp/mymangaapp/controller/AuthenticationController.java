package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.RefreshRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mymangaapp.mymangaapp.dto.request.AuthenticationRequest;
import com.example.mymangaapp.mymangaapp.dto.request.IntrospectRequest;
import com.example.mymangaapp.mymangaapp.dto.request.LogoutRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.AuthenticationResponse;
import com.example.mymangaapp.mymangaapp.dto.response.IntrospectResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {

        AuthenticationResponse response = authenticationService.login(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();

    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {

        IntrospectResponse response = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(response)
                .build();

    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) {

        authenticationService.logout(request);

        return ApiResponse.<Void>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request) {

        AuthenticationResponse response = authenticationService.refresh(request);

        return  ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }

}
