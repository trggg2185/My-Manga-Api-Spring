package com.example.mymangaapp.mymangaapp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

// Config giúp tải file lua khi start app
@Configuration
public class RateLimitConfig {

    private static final String PATH = "rate_limit.lua";

    // Long là giá trị trả về của file lua
    @Bean
    DefaultRedisScript<Long> rateLimitingScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        // trỏ tới file lua trong resource
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(PATH)));
        // set kiểu trả về của file lua
        redisScript.setResultType(Long.class);

        return redisScript;
    }
}
