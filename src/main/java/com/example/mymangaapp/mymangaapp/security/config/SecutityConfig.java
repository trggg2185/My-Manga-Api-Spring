package com.example.mymangaapp.mymangaapp.security.config;

import com.example.mymangaapp.mymangaapp.security.JwtAuthenticationEntryPoint;
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
            "/users",
            "/auth/login",
            "/auth/introspect",
            "/auth/logout",
            "/auth/refresh",
    };

    static String[] GET_PUBLIC_ENDPOINTS = {
            "/transgroups",
            "/mangas/{id}"
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
