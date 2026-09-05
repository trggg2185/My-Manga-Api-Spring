package com.example.mymangaapp.mymangaapp.security.config;

import com.example.mymangaapp.mymangaapp.security.jwt.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.mymangaapp.mymangaapp.security.filter.JwtAuthenticationFilter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecutityConfig {

    JwtAuthenticationFilter jwtAuthenticationFilter;

    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    static String[] POST_PUBLIC_ENDPOINTS = {
            "/users", // tạo acc mới cho user
            "/auth/login", // đăng nhập
            "/auth/introspect", // check nhanh token còn valid k
            "/auth/logout", // đăng xuất
            "/auth/refresh" // refresh token
    };

    static String[] GET_PUBLIC_ENDPOINTS = {
            "/transgroups", // lấy tất cả các nhóm dịch đang hoạt động (APPROVED)
            "/mangas/{id}", // lấy info manga theo id
            "/transgroups/{id}/mangas", // lấy các manga đang dịch của nhóm
            "/mangas/{id}/chapters", // lấy các chapter của 1 manga
            "/chapters/{id}/pages" // lấy các ảnh của 1 chapter
    };

    static String ADMIN = "ADMIN";

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // <=> csrf -> csrf.disable()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, POST_PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, GET_PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/admin/**").hasRole(ADMIN)
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Đăng ký jwtAuthenticationFilter chạy TRƯỚC
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
