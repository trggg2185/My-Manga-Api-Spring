package com.example.mymangaapp.mymangaapp.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.mymangaapp.mymangaapp.entity.Role;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.repository.RoleRepository;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InitApplicationConfig {
    
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${app.init.admin.username}")
    String defaultAdminUsername;

    @NonFinal
    @Value("${app.init.admin.password}")
    String defaultAdminPassword;

    @NonFinal
    @Value("${app.init.admin.email}")
    String defaultAdminEmail;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            log.info("................Init application starts...............");

            if (!userRepository.existsByUsername(defaultAdminUsername)) {

                Role role = roleRepository
                        .findById("ADMIN")
                        .orElseThrow(() -> new AppException(ResponseCode.ROLE_NOT_FOUND));

                User user = User.builder()
                        .username(defaultAdminUsername)
                        .password(passwordEncoder.encode(defaultAdminPassword))
                        .email(defaultAdminEmail)
                        .roles(Set.of(role))
                        .build();

                userRepository.save(user);
                log.info("Create admin successfully! Default password: {}", defaultAdminPassword);
            }

            log.info(".................Init application ends...................");
        };
    }

}
