package com.example.mymangaapp.mymangaapp.utils;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        Authentication authentication = SecurityUtils.getAuthentication();

        return authentication.getName();
    }

    public static boolean hasRole(String roleName) {
        Authentication authentication = SecurityUtils.getAuthentication();

        String targetRole = roleName.startsWith("ROLE_") ? roleName : ("ROLE_" + roleName);

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority
                        .getAuthority()
                        .equals(targetRole));
    }

    public static boolean hasPermission(String permission) {
        // Chỉ cần có role admin là pass hết quyền
        if (SecurityUtils.hasRole("ADMIN"))
            return true;

        return SecurityUtils
                .getAuthentication()
                .getAuthorities()
                .contains(new SimpleGrantedAuthority(permission));
    }


}
