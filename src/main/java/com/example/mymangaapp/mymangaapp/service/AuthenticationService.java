package com.example.mymangaapp.mymangaapp.service;

import java.util.Date;

import org.springframework.lang.NonNull;
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
import com.example.mymangaapp.mymangaapp.security.utils.JwtUtils;

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

    // Đôi với những hàm khác ngoài introspect sẽ coi việc xác thưc token trả về false
    // là 1 lỗi ứng dụng nên khi false sẽ ném ra ngoại lệ luôn
    
    // Đăng nhập (tạo access token)
    public AuthenticationResponse login(@NonNull AuthenticationRequest request) {

        User user = userRepository
                .findWithDetailsByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ResponseCode.UNAUTHENTICATED);

        String token = jwtUtils.generateAccessToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();

    }

    // Check token hợp lệ
    public IntrospectResponse introspect(@NonNull IntrospectRequest request) {

        // introspect sẽ trả về kết quả true hoặc false cho token
        boolean valid = jwtUtils.verifyToken(request.getToken());

        return IntrospectResponse.builder()
                .valid(valid)
                .build();

    }

    // Đăng xuất (vô hiệu hoá token)
    public void logout(@NonNull LogoutRequest request) {

        String jwtId = jwtUtils.extractJwtId(request.getToken());

        if (invalidatedTokenRepository.existsById(jwtId)) {
            throw new AppException(ResponseCode.UNAUTHENTICATED);
        }

        Date expirationTime = jwtUtils.extractExpirationTime(request.getToken());

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expirationTime(expirationTime.toInstant())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

    }

    // Refresh token mới
    public AuthenticationResponse refresh(@NonNull RefreshRequest request) {

        // Xác thực token cũ ổn không
        if (!jwtUtils.verifyToken(request.getToken(), true)) {
            throw new AppException(ResponseCode.UNAUTHENTICATED);
        }

        // Nếu ổn thì đưa token cũ vào table invalidated token
        String jwtId = jwtUtils.extractJwtId(request.getToken());
        Date expirationTime = jwtUtils.extractExpirationTime(request.getToken());

        invalidatedTokenRepository.save(InvalidatedToken.builder()
                .id(jwtId)
                .expirationTime(expirationTime.toInstant())
                .build());

        String username = jwtUtils.extractUsername(request.getToken());

        User user = userRepository.findWithDetailsById(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(jwtUtils.generateAccessToken(user))
                .build();
    }

}
