package com.example.mymangaapp.mymangaapp.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChapterViewService {

    StringRedisTemplate redisTemplate;

    // Hash đang nhận view mới: field = chapterId, value = số view chờ flush
    static String PENDING_KEY = "mymangaapp:views:chapter";

    // Hash đã được "chốt" để flush (RENAME từ PENDING_KEY)
    static String PROCESSING_KEY = "mymangaapp:views:chapter:processing";
    static String LOCK_VIEW_KEY = "mymangaapp:views-lock:chapter";

    // Khoá view của 1 người đọc trong 30 phút, sau đó đọc lại mới tính thêm
    static Duration LOCK_VIEW_TIMEOUT = Duration.ofMinutes(30);

    public void incrementView(String chapterId, String userId, String clientId) {

        try {
            doIncrement(chapterId, userId, clientId);
        } catch (Exception e) {
            // cô tình bắt exception rồi log, để ko làm ảnh hưởng đọc truyện của user
            log.warn("Lỗi không tăng view được cho chapter {}: {}", chapterId, e.getMessage());
        }

    }

    public void doIncrement(String chapterId, String userId, String clientId) {
        // Đăng nhập thì chặn theo user, không thì chặn theo ip
        String viewer;
        if (userId != null) {
            viewer = "user:" + userId;
        } else if (clientId != null && !clientId.isBlank()) {
            viewer = "ip:" + clientId;
        } else {
            log.warn("Không xác định được người đọc, bỏ qua tính view. chapter: {}", chapterId);
            return;
        }

        String lockKey = LOCK_VIEW_KEY + ":" + chapterId + ":" + viewer;

        // Key chưa tồn tại thì set và trả về true (lượt xem mới), tồn tại rồi thì false
        Boolean isNewView = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_VIEW_TIMEOUT);

        if (Boolean.TRUE.equals(isNewView)) {
            redisTemplate.opsForHash().increment(PENDING_KEY, chapterId, 1);
            log.debug("Tính thêm view cho chapter {}", chapterId);
        } else {
            log.debug("Không tính view vì đã tính trong {} phút: {}", LOCK_VIEW_TIMEOUT.toMinutes(), lockKey);
        }
    }

    /**
     * Chốt lô cần flush. Nếu lần trước còn dở (PROCESSING_KEY còn tồn tại) thì xử lý tiếp lô đó,
     * vì renameIfAbsent từ chối ghi đè nên không bao giờ làm mất dữ liệu cũ.
     */
    public Map<String, Long> claimPendingViews() {
        try {
            redisTemplate.renameIfAbsent(PENDING_KEY, PROCESSING_KEY);   // RENAMENX: nguyên tử
        } catch (RedisSystemException e) {
            // "no such key": không có view nào đang chờ. Bình thường, bỏ qua.
        }

        Map<Object, Object> raw = redisTemplate.opsForHash().entries(PROCESSING_KEY);
        Map<String, Long> result = HashMap.newHashMap(raw.size());

        raw.forEach((key, value) -> {
            try {
                result.put(key.toString(), Long.parseLong(value.toString()));
            } catch (NumberFormatException e) {
                // Một giá trị hỏng không được làm hỏng cả lượt flush
                log.error("Bỏ qua view có giá trị không hợp lệ, chapter: {}, value: {}", key, value);
            }
        });

        return result;
    }

    // Xóa các chương đã flush xong khỏi lô đang xử lý. Hash rỗng thì Redis tự xóa key.
    public void removeProcessed(Collection<String> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) {
            return;
        }

        redisTemplate.opsForHash().delete(PROCESSING_KEY, chapterIds.toArray());
        log.debug("Đã xóa {} chương khỏi lô processing", chapterIds.size());
    }

}
