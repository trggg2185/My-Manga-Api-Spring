package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

// Giao tiếp với aws sdk để cung cấp các phương thức
// tương tác với file: lưu trữ, xoá, ...
// cho controller và các services khác dùng
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageService {

    S3Client s3Client;

    @Value("${cloud.r2.bucket}")
    @NonFinal
    String bucketName;

    @Value("${cloud.r2.s3.public-url}")
    @NonFinal
    String publicUrl;

    // upload 1 file, yc có role translator
    @PreAuthorize("hasRole('TRANSLATOR') or hasRole('ADMIN')")
    public String uploadTmpFile(@NotNull MultipartFile file) {

        try {
            // lấy tên file
            String originalFileName = file.getOriginalFilename();
            log.info("Original file name: {}", originalFileName);

            // lấy đuôi file (.jpg, .png, ...)
            String extension = StringUtils.getFilenameExtension(originalFileName);

            String filename = UUID.randomUUID() + "." + extension;

            // tạo url folder theo ngày
            String dateFolder = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // key chính là url folder của ảnh được upload có dạng: /tmp/2026-08-21/image.jpg
            String objectKey = "tmp/" + dateFolder + "/" + filename;

            // upload request
            PutObjectRequest putObjRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            // upload lên r2 bằng stream
            s3Client.putObject(
                    putObjRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // trả về url cho frontend truy cập để hiển thị
            return generatePublicUrl(objectKey);
        }
        catch (IOException exception) {
            throw new AppException(ResponseCode.FILE_UPLOAD_FAILED);
        }

    }

    // upload nhiều files
    @PreAuthorize("hasRole('TRANSLATOR') or hasRole('ADMIN')")
    public List<String> uploadMultiTmpFiles(@NotNull List<MultipartFile> files) {

        // Thay vì dùng foreach lặp tuần tự mất thời gian
        // ta dùng luồng song song chạy nhanh hơn
        return files.parallelStream()
                .map(this::uploadTmpFile)
                .toList();

    }

    public String copyFile(String sourceKey, String destinationKey, boolean deleteSource) {

        CopyObjectRequest copyObjRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(sourceKey)
                .destinationBucket(bucketName)
                .destinationKey(destinationKey)
                .build();

        s3Client.copyObject(copyObjRequest);

        log.info("Copy file thành công! Từ {} sang {}", sourceKey, destinationKey);

        // Nếu hàm có option xoá file tmp cũ sau khi copy file thành công
        if (deleteSource) deleteFile(sourceKey);

        return generatePublicUrl(destinationKey);
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjRequest);
        log.info("Đã xoá file temp: {}", key);
    }

    // method này ko chỉ dùng để dọn files trong tmp/ quá 3 tiếng
    // còn dùng để xoá các files với prefix nữa (dùng Instant.now())
    public int deleteFilesWithPrefix(String prefix, Instant thresholdTime) {

        // request lấy ds trong prefix
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        // Gọi r2 lấy ds, nếu quá dài thì auto phân trang
        ListObjectsV2Iterable paginator = s3Client.listObjectsV2Paginator(listRequest);

        // số luọng file đã xoá
        int deletedCount = 0;

        // Duyệt qua ds
        for (S3Object s3Object : paginator.contents()) {

            // file nào đc tạo ta với thoigian trc thoigian 3 tiếng trc
            // tức là quá 3 tiếng kể từ lúc file đó được up lên r2 thì dọn luôn
            if (s3Object.lastModified().isBefore(thresholdTime)) {
                deleteFile(s3Object.key());
                deletedCount++;
            }
        }

        return deletedCount;
    }

    public String generatePublicUrl(String key) {
        return publicUrl + "/" + key;
    }

}
