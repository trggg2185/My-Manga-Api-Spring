package com.example.mymangaapp.mymangaapp.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

// Cấu hình CORS cho frontend truy cập
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    List<String> allowedOrigins;

    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(allowedOrigins);

        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");

        // cho frontend gửi cookie và header xác thực
        // nếu để true thì allowed-origin ko đc để * nhé
        corsConfiguration.setAllowCredentials(true);

        // giúp header-control-max-age có tg sống lâu hơn khi trình duyệt lưu cache
        // giúp các requests sau request đầu tiên có tốc độ nhanh hơn
        // do khi call api trình duyệt thấy cache đã được lưu và còn sống, nên sẽ ko phải xác thực cors mỗi requests
        corsConfiguration.setMaxAge(3600L); // cache sống 1h (3600s)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
