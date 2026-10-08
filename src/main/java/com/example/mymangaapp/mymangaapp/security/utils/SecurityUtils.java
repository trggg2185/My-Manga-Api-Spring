package com.example.mymangaapp.mymangaapp.security.utils;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.security.service.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {}

    // Trả về Optional.empty() nếu chưa đăng nhập / anonymous. Không ném exception.
    public static Optional<Authentication> findAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.of(auth);
    }

    // Dùng cho code bắt buộc phải đăng nhập, ko đăng nhập bắn lỗi ngay
    public static Authentication getAuthentication() {
        return findAuthentication()
                .orElseThrow(() -> new AppException(ResponseCode.UNAUTHENTICATED));
    }

    public static String getCurrentUsername() {
        return getAuthentication().getName();
    }

    // Bắt buộc đăng nhập, ném UNAUTHENTICATED nếu không có
    public static String getCurrentUserId() {
        return findCurrentUserId().orElseThrow(() -> new AppException(ResponseCode.UNAUTHENTICATED));
    }

    // Không ném lỗi, dùng cho những chỗ user có thể chưa đăng nhập (rate limit, log...)
    public static Optional<String> findCurrentUserId() {
        return findAuthentication()
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast)
                .map(CustomUserDetails::getId);
    }

    // check user đang đăng nhập là admin
    public static boolean isAdmin() {
        return findAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())))
                .orElse(false);
    }

}
