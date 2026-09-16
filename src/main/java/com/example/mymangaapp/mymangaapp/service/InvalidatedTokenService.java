package com.example.mymangaapp.mymangaapp.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// Service dùng để lưu trữ các token invalidated bằng redis
public class InvalidatedTokenService {

    StringRedisTemplate redisTemplate;

    static String PREFIX = "invalidated-token:jti:";

    public void invalidateToken(String token, long ttlInSeconds) {
        if (ttlInSeconds > 0) {
            String key = PREFIX + token;
            redisTemplate.opsForValue().set(key, "invalidated", ttlInSeconds, TimeUnit.SECONDS);
        }
    }

    public boolean isTokenInvalidated(String token) {

        String key = PREFIX + token;

        return redisTemplate.hasKey(key);
    }

}
