package com.example.mymangaapp.mymangaapp.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

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

    @NonFinal
    @Value("${cloud.r2.bucket}")
    String bucketName;

    S3Client s3Client;

    // Cron expression: second - minute - hour - day of month - month - day of week
    // 2 số 0 ở đầu nghĩa là chạy vào lúc 0 giây 0 phút của mọi giờ, mọi ngày
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupTmpFiles() {
        log.info("Dọn dẹp tmp r2.................");

        try {
            // lấy tg lúc 3 tiếng trc
            Instant thresholdTime = Instant.now().minus(3, ChronoUnit.HOURS);

            // prefix sẽ vào tmp rồi vào folder theo ngày rồi xoá các files tmp trong đó
            String dateFolder = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String prefix = "tmp/" + dateFolder + "/";

            // Lấy ds trong tmp
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();

            // Gọi r2 lấy ds, nếu quá dài thì auto phân trang
            ListObjectsV2Iterable paginator = s3Client.listObjectsV2Paginator(listRequest);

            // số luọng file đã xoá
            int deletedCount = 0;

            // Duyệt qua ds trong tmp, quá 3 tiếng là xoá
            for (S3Object s3Object : paginator.contents()) {

                // file nào đc tạo ta với thoigian trc thoigian 3 tiếng trc
                // tức là quá 3 tiếng kể từ lúc file đó được up lên r2 thì dọn luôn
                if (s3Object.lastModified().isBefore(thresholdTime)) {
                    deleteFile(s3Object.key());
                    deletedCount++;
                }
            }

            log.info("Dọn tmp r2 thành công! Đã dọn {} files", deletedCount);

        } catch (Exception e) {
            log.error("Lỗi dọn dẹp tmp r2!", e);
        }

    }

    private void deleteFile(String key) {
        DeleteObjectRequest deleteObjRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjRequest);
        log.info("Đã xoá file temp: {}", key);
    }

}
