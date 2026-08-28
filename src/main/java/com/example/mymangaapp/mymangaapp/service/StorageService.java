package com.example.mymangaapp.mymangaapp.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDate;
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

    // upload 1 file
    public String uploadTmpFile(MultipartFile file) {

        try {
            // lấy tên file
            String originalFileName = file.getOriginalFilename();
            log.info("Original file name: {}", originalFileName);

            // lấy đuôi file (.jpg, .png, ...)
            String extension = StringUtils.getFilenameExtension(originalFileName);

            String filename = UUID.randomUUID() + "." + extension;

            // tạo url folder theo ngày
            String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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
            return publicUrl + "/" + objectKey;
        }
        catch (IOException exception) {
            throw new RuntimeException("Lỗi liên quan đến IO!", exception);
        }

    }

    // upload nhiều files
    public List<String> uploadMultiTmpFiles(List<MultipartFile> files) {


        return null;
    }

}
