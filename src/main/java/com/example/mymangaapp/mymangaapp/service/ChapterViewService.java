package com.example.mymangaapp.mymangaapp.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChapterViewService {

    StringRedisTemplate redisTemplate;

    static String REDIS_HASH_KEY = "mymangaapp:views:chapter";
    static String LOCK_VIEW_KEY = "mymangaapp:views-lock:chapter";

    // Khoá views của 1 user khi đọc trong 30p, sau 30p đọc lại tính thêm view
    static Duration LOCK_VIEW_TIMEOUT = Duration.ofMinutes(30);

    public void incrementView(String chapterId, String userId, String clientId) {

        String lockKey = LOCK_VIEW_KEY + ":" + chapterId + ":";

        // nếu user đăng nhập thì chặn theo user id
        if (userId != null) {
            lockKey = lockKey + ":" + userId;
        } else { // ko đăng nhập chặn theo ip
            lockKey = lockKey + ":" + clientId;
        }

        // lock view của người dùng trong vòng 30min
        // nếu key chưa tồn tại thì set key và trả về true, tồn tại rồi thì false
        Boolean isNewView = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_VIEW_TIMEOUT);

        if (Boolean.TRUE.equals(isNewView)) {
            Long result = redisTemplate.opsForHash().increment(REDIS_HASH_KEY, chapterId, 1);

            log.info("Kết quả tăng view: {}", result);
        } else {
            log.info("Không tính view nữa vì đã tính rồi! chapter: {}, lock key: {}", chapterId, lockKey);
        }

    }

    // lấy các views trong redis đg chờ được flush xuống db
    public Map<String, Long> getAllPendingViews() {
        // Lấy tất các các field-value trong hash
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(REDIS_HASH_KEY);

        Map<String, Long> map = new HashMap<>();

        // convert sang string-long
        // key là chapter id, value là views
        raw.forEach((key, value) ->
                map.put(key.toString(), Long.parseLong(value.toString())));

        return map;
    }

    // clear các cặp chapterId-views trong hash đã được flush xuống db
    public void clearViews(Set<String> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) {
            return;
        }

        redisTemplate.opsForHash().delete(REDIS_HASH_KEY, chapterIds.toArray());
        log.info("Clear {} field-value trong hash redis thành công!", chapterIds.size());
    }

}
