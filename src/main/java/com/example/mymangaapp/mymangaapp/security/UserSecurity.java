package com.example.mymangaapp.mymangaapp.security;

import com.example.mymangaapp.mymangaapp.repository.UserRepository;
import com.example.mymangaapp.mymangaapp.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("userSec")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSecurity {

    UserRepository userRepository;

    // Check chính mình hoặc admin
    public boolean isSelfOrAdmin(String userId) {
        if (SecurityUtils.isAdmin()) {
            return true;
        }

        String id = SecurityUtils.getCurrentUserId();

        return userId.equals(id);
    }
}
