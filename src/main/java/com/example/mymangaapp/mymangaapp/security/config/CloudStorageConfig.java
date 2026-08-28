package com.example.mymangaapp.mymangaapp.security.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

// Cấu hình cho dịch vụ lưu trũ cloud r2 object storage của cloudflare
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudStorageConfig {

    @Value("${cloud.r2.credentials.access-key-id}")
    String accessKeyId;

    @Value("${cloud.r2.credentials.secret-access-key}")
    String secretAccessKey;

    @Value("${cloud.r2.s3.endpoint-url}")
    String endpointUrl;

    @Value("${cloud.r2.region.static}")
    String region;

    /*
    * - S3 client là main object trong aws sdk, để làm gateway tương tác với dịch vụ s3-compatible như r2 object storage
    * - Nó cung cấp các method như:
    *   + upload file
    *   + down file
    *   + xoá file
    *   + lấy file
    *   + tạo, xoá bucket
    * */
    @Bean
    S3Client s3Client() {
        // Đối tượng chứa thông tin xác thực
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey
        );

        return S3Client.builder()
                // ghi đè endpoint của aws thành r2, để sdk gửi đến đúng server r2
                .endpointOverride(URI.create(endpointUrl))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
