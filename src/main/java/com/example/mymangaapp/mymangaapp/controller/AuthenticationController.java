package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.annotation.RateLimit;
import com.example.mymangaapp.mymangaapp.dto.auth.request.AuthenticationRequest;
import com.example.mymangaapp.mymangaapp.dto.auth.request.IntrospectRequest;
import com.example.mymangaapp.mymangaapp.dto.auth.request.LogoutRequest;
import com.example.mymangaapp.mymangaapp.dto.auth.request.RefreshRequest;
import com.example.mymangaapp.mymangaapp.dto.auth.request.RegisterRequest;
import com.example.mymangaapp.mymangaapp.dto.user.response.CurrentUserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mymangaapp.mymangaapp.dto.common.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.auth.response.AuthenticationResponse;
import com.example.mymangaapp.mymangaapp.dto.auth.response.IntrospectResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.enums.LimitType;
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


    // ----------------------------endpoint public (cho khách) ----------------------------------//

    @PostMapping("/register")
    public ApiResponse<CurrentUserResponse> register(@RequestBody @Valid RegisterRequest request) {

        CurrentUserResponse response = authenticationService.register(request);

        return ApiResponse.<CurrentUserResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping("/login")
    @RateLimit(resetTimeInSeconds = 900)
    public ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {

        AuthenticationResponse response = authenticationService.login(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();

    }

    @PostMapping("/introspect")
    @RateLimit(capacity = 60)
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request) {

        IntrospectResponse response = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(response)
                .build();

    }

    @PostMapping("/logout")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request) {

        authenticationService.logout(request);

        return ApiResponse.<Void>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/refresh")
    @RateLimit(capacity = 10, limitType = LimitType.USER_ID)
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody @Valid RefreshRequest request) {

        AuthenticationResponse response = authenticationService.refresh(request);

        return  ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }

}
