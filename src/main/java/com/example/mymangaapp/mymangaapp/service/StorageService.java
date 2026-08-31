package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

// Cung cấp các phương thức tương tác với file: lưu trữ, xoá, ...
// cho các services khác dùng
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageService {

    final S3Client s3Client;

    @Value("${cloud.r2.bucket}")
    String bucketName;

    @Value("${cloud.r2.s3.public-url}")
    String publicUrl;

    // upload 1 file, yc có role translator
    @PreAuthorize("hasRole('TRANSLATOR')")
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
    @PreAuthorize("hasRole('TRANSLATOR')")
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

        CopyObjectResponse copyObjResponse = s3Client.copyObject(copyObjRequest);

        // Nếu copy thành công
        if (copyObjResponse.copyObjectResult() != null) {
            log.info("Copy file thành công! Từ {} sang {}", sourceKey, destinationKey);

            // Nếu hàm có option xoá file tmp cũ sau khi copy file thành công
            if (deleteSource) deleteFile(sourceKey);

            return generatePublicUrl(destinationKey);
        } else {
            throw new AppException(ResponseCode.FILE_COPY_FAILED);
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

    private String generatePublicUrl(String key) {
        return publicUrl + "/" + key;
    }

}
