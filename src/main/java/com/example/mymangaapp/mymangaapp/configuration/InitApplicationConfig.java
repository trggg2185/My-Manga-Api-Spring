package com.example.mymangaapp.mymangaapp.configuration;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.mymangaapp.mymangaapp.entity.Permission;
import com.example.mymangaapp.mymangaapp.entity.Role;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.repository.PermissionRepository;
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
    ApplicationRunner applicationRunner(
                UserRepository userRepository,
                RoleRepository roleRepository,
                PermissionRepository permissionRepository
    ) {
        return args -> {
            log.info("................Init application starts...............");

            Permission createPost = permissionRepository
                    .findById("CREATE_POST")
                    .orElseGet(() -> permissionRepository.save(Permission.builder()
                            .name("CREATE_POST")
                            .description("create new post permission")
                            .build()));

            Permission deleteGroup = permissionRepository
                    .findById("DELETE_GROUP")
                    .orElseGet(() -> permissionRepository.save(Permission.builder()
                            .name("DELETE_GROUP")
                            .description("delete trans group permission")
                            .build()));

            Permission createManga = permissionRepository
                    .findById("CREATE_MANGA")
                    .orElseGet(() -> permissionRepository.save(Permission.builder()
                            .name("CREATE_MANGA")
                            .description("create new manga permission")
                            .build()));

            Permission updateManga = permissionRepository
                    .findById("UPDATE_MANGA")
                    .orElseGet(() -> permissionRepository.save(Permission.builder()
                            .name("UPDATE_MANGA")
                            .description("update manga permission")
                            .build()));

            Role userRole = roleRepository
                    .findById("USER")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name("USER")
                            .description("Role user")
                            .permissions(Set.of(createPost))
                            .build()));

            Role adminRole = roleRepository
                    .findById("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name("ADMIN")
                            .description("Role admin")
                            .permissions(Set.of(deleteGroup))
                            .build()));

            Role translatorRole = roleRepository
                    .findById("TRANSLATOR")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name("TRANSLATOR")
                            .description("Role translator")
                            .permissions(Set.of(createManga, updateManga))
                            .build()));

            if (!userRepository.existsByUsername(defaultAdminUsername)) {
                User admin = User.builder()
                        .username(defaultAdminUsername)
                        .password(passwordEncoder.encode(defaultAdminPassword))
                        .email(defaultAdminEmail)
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(admin);
                log.info("Create admin successfully! Default password: {}", defaultAdminPassword);
            }

            log.info(".................Init application ends...................");
        };
    }

}
