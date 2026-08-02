package com.example.mymangaapp.mymangaapp.service;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mymangaapp.mymangaapp.dto.request.AuthenticationRequest;
import com.example.mymangaapp.mymangaapp.dto.request.IntrospectRequest;
import com.example.mymangaapp.mymangaapp.dto.request.LogoutRequest;
import com.example.mymangaapp.mymangaapp.dto.request.RefreshRequest;
import com.example.mymangaapp.mymangaapp.dto.response.AuthenticationResponse;
import com.example.mymangaapp.mymangaapp.dto.response.IntrospectResponse;
import com.example.mymangaapp.mymangaapp.entity.InvalidatedToken;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.repository.InvalidatedTokenRepository;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;
import com.example.mymangaapp.mymangaapp.utils.JwtUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    PasswordEncoder passwordEncoder;

    JwtUtils jwtUtils;
    
    // Đăng nhập (tạo access token)
    public AuthenticationResponse login(AuthenticationRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ResponseCode.UNAUTHENTICATED);

        String token = jwtUtils.generateAccessToken(user);

        return AuthenticationResponse.builder()
                .authenticated(authenticated)
                .token(token)
                .build();

    }

    // Check token hợp lệ
    public IntrospectResponse introspect(IntrospectRequest request) {

        boolean valid = jwtUtils.verifyToken(request.getToken());

        return IntrospectResponse.builder()
                .valid(valid)
                .build();

    }

    // Đăng xuất (vô hiệu hoá token)
    public void logout(LogoutRequest request) {
            
        String jwtId = jwtUtils.extractJwtId(request.getToken());
        Date expirationTime = jwtUtils.extractExpirationTime(request.getToken());

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expirationTime(expirationTime.toInstant())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

    }

    // Refresh token mới
    public AuthenticationResponse refresh(RefreshRequest request) {

        return null;
    }

}
