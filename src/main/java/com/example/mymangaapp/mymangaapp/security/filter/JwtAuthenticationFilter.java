package com.example.mymangaapp.mymangaapp.security.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.mymangaapp.mymangaapp.repository.InvalidatedTokenRepository;
import com.example.mymangaapp.mymangaapp.security.CustomUserDetailsService;
import com.example.mymangaapp.mymangaapp.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// OncePerRequestFilter là 1 filer trong filter chain
// Cứ mỗi http request đến là nó chạy 1 phát
// Để phục vụ cho việc authenticate jwt token xem có hợp lệ không
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
// Dù là public endpoint vẫn đi qua filter này nhé
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtUtils jwtUtils;
    CustomUserDetailsService customUserDetailsService;

    InvalidatedTokenRepository invalidatedTokenRepository;

    // Dùng để đẩy Exception ở Filter về cho GlobalExceptionHandler
    // (@RestControllerAdvice) xử lý
    HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(
            JwtUtils jwtUtils,
            CustomUserDetailsService customUserDetailsService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            InvalidatedTokenRepository invalidatedTokenRepository) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.resolver = resolver;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = parseToken(request);

            // Check token ko null và có hợp lệ (chuẩn chữ ký, chưa hết hạn)
            // Nếu là public endpoint thì sẽ ko có header authorization 
            // nên token sẽ null và chạy qua luôn filter này, đền tầng authorization filter
            // vì ko có gì trong context đáng nhẽ bị chặn nhưng nó là public endpoint 
            // nên cứ thể qua tầng authorization filter
            if (
                token != null &&
                jwtUtils.verifyToken(token) && 
                invalidatedTokenRepository.existsById(jwtUtils.extractJwtId(token))
            ) {

                // Lấy username từ token
                String username = jwtUtils.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Lấy thông tin UserDetails (bao gồm cả Authorities/Permissions) từ db
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    // Tạo đối tượng Authentication chuẩn của Spring Security
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credentials để null vì đã xác thực qua Token
                            userDetails.getAuthorities() // Danh sách Role & Permission
                    );

                    // Đính kèm thêm thông tin phụ (IP, SessionId...) từ Request
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Nap authentiaction vao context
                    // request này chính thức đăng nhập thành công
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            // Cho phép request đi tiếp tới các filter khác để đi vào controller
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Nếu có lỗi Token hết hạn, sai chữ ký... bắn lỗi cho @RestControllerAdvice
            // hứng
            resolver.resolveException(request, response, null, e);
        }

    }

    // Hàm phụ trợ: Bóc tách chuỗi "Bearer <TOKEN>" từ Header Authorization
    private String parseToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // Lấy chuỗi sau 7 chữ cái đầu Bearer + space, chính là jwt token
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

}
