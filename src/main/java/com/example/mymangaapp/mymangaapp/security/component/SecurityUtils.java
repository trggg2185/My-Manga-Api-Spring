package com.example.mymangaapp.mymangaapp.security.component;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.security.service.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Slf4j
public class SecurityUtils {

    private SecurityUtils() {}

    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            throw new AppException(ResponseCode.UNAUTHENTICATED);

        return authentication;
    }

    public static String getCurrentUsername() {
        return getAuthentication().getName();
    }

    public static String getCurrentUserId() {
        CustomUserDetails customUserDetails = (CustomUserDetails) getAuthentication().getPrincipal();

        return customUserDetails.getId();
    }

    // check nhanh user hiện tại là admin không
    public static boolean isAdmin() {
        return getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority
                        .getAuthority()
                        .equals("ROLE_ADMIN"));
    }

}
