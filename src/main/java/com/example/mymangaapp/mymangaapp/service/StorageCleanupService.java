package com.example.mymangaapp.mymangaapp.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StorageCleanupService {

    StorageService storageService;

    // Cron expression: second - minute - hour - day of month - month - day of week
    // 2 số 0 ở đầu nghĩa là chạy vào lúc 0 giây 0 phút của mọi giờ, mọi ngày
    @Scheduled(cron = "0 43 * * * *")
    public void cleanupTmpFiles() {
        log.info("Dọn dẹp tmp r2.................");

        try {
            // lấy tg lúc 3 tiếng trc
            Instant thresholdTime = Instant.now().minus(3, ChronoUnit.HOURS);

            // prefix sẽ vào tmp rồi vào folder theo ngày rồi xoá các files tmp trong đó
            String dateFolder = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String prefix = "tmp/" + dateFolder + "/";

            // gọi hàm xoá bên storage service
            storageService.deleteFilesWithPrefix(prefix, thresholdTime);

        } catch (Exception e) {
            log.error("Lỗi dọn dẹp tmp r2!", e);
        }

    }

    // Chạy vào lúc 3h sáng mỗi ngày để dọn dẹp các files đã chuyển từ tmp sang mangas
    // nhưng bị lỗi trong qtrình copy
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupFailedUploadedFiles() {
        log.info("Dọn dẹp mangas r2.................");

        // Chưa làm được, làm sau
    }

}
