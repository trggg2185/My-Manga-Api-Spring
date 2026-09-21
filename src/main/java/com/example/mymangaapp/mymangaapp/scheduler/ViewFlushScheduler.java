package com.example.mymangaapp.mymangaapp.scheduler;

import com.example.mymangaapp.mymangaapp.service.ChapterViewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ViewFlushScheduler {

    ChapterViewService chapterViewService;

    // Spring auto tạo bean và auto inject field này, ko cần config
    // dùng jdbc template để gom lô batch update xuống db 1 thể
    // giống việc gom 1 loạt các object key để batch delete trên r2 cloud
    JdbcTemplate jdbcTemplate;

    // Mỗi 5p lại flush view từ redis xuống db 1 lần
    @Scheduled(cron = "0 */5 * * * *")
    public void flushViews() {
        Map<String, Long> pendingViews = chapterViewService.getAllPendingViews();

        if (pendingViews.isEmpty()) {
            log.info("Không có views để flush xuống db!");
            return;
        }

        // chia thành các lô hàng, mỗi lô chứa 50 chapterId - views
        List<Map<String, Long>> batches = chunk(pendingViews, 50);

        for (Map<String, Long> batch : batches) {

            // flush xuống db theo từng lô
            executeBatchUpdateViews(batch);

            log.info("Flush views thành công cho {} chương", batch.size());

            // flush xong thì clear trong redis
            chapterViewService.clearViews(batch.keySet());
        }
    }

    // hàm trực tiếp flush 1 lô xuống db
    private void executeBatchUpdateViews(Map<String, Long> batch) {

        // nhớ là + thêm views chứ ko phải set views = ?
        String sql = "UPDATE chapter SET views = views + ? WHERE id = ?";

        List<Object[]> batchArgs = new ArrayList<>();

        // cách chuẩn để duyệt qua từng cặp key-valud trong map
        for (Map.Entry<String, Long> entry : batch.entrySet()) {
            // ? của views đứng trc thì value trc, ? của id sau thì key sau
            batchArgs.add(new Object[]{ entry.getValue(), entry.getKey() });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    // gom map các chapterId - views theo lô với mỗi lô kích thước là size (size là số chương)
    private List<Map<String, Long>> chunk(Map<String, Long> map, int size) {
        List<Map<String, Long>> result = new ArrayList<>();

        List<String> keys = new  ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i += size) {
            // tạo 1 lô để chứa các giá trị
            Map<String, Long> batch = new HashMap<>();

            for (int j = i; j < Math.min(i + size, keys.size()); j++) {
                batch.put(keys.get(j), map.get(keys.get(j)));
            }

            result.add(batch);
        }

        return result;
    }

}
