package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    // Các đuôi file chấp nhận cho upload
    static List<String> VALID_EXTENSIONS = List.of("jpg", "png", "webp");
    // đuôi các file sẽ upload lên r2
    static String FIXED_EXTENSION = "webp";
    // content type
    static String FIXED_CONTENT_TYPE = "image/webp";
    // width các file khi up lên r2
    static int FIXED_TARGET_WIDTH = 1200;
    // chất lg các file khi up lên r2
    static float FIXED_QUALITY = 0.9f;
    // đây là kích thước tối đa của file sau khi optimize (1MB)
    static long MAX_BYPASS_SIZE = 1024 * 1024;

    // upload 1 file, yc có role translator
    @PreAuthorize("hasRole('TRANSLATOR') or hasRole('ADMIN')")
    public String uploadTmpFile(@NonNull MultipartFile file) {

        // Check file rỗng
        if (file.isEmpty()) {
            throw new AppException(ResponseCode.FILE_REQUIRED);
        }

        try {
            // lấy tên file
            String originalFileName = file.getOriginalFilename();
            // lấy đuôi file (jpg, png, ...)
            String rawExtension = StringUtils.getFilenameExtension(originalFileName);
            if (rawExtension == null) {
                throw new AppException(ResponseCode.FILE_INVALID);
            }

            // đưa hết về chuỗi viếtthường
            String extension = rawExtension.toLowerCase();
            // Chỉ cho phép 3 loại file jpg, png, webp
            if (!VALID_EXTENSIONS.contains(extension)) {
                throw new AppException(ResponseCode.FILE_INVALID);
            }

            String filename = UUID.randomUUID() + "." + FIXED_EXTENSION;
            // tạo url folder theo ngày
            String dateFolder = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String prefix = "tmp/" + dateFolder + "/";

            // key chính là url folder của ảnh được upload có dạng: /tmp/2026-08-21/image.jpg
            String objectKey = prefix + filename;
            log.info("Object key: {}", objectKey);

            byte[] optimizedImageBytes;

            // nếu mà file đã là webp và có kích thước <= 1MB
            // thì ta sẽ lấy luôn file đó up lên r2 luôn mà ko optimize nữa (tránh optimize kép)
            if (extension.equals(FIXED_EXTENSION) && file.getSize() <= MAX_BYPASS_SIZE) {
                optimizedImageBytes = file.getBytes();
            } else {
                optimizedImageBytes = optimizeImage(file, FIXED_TARGET_WIDTH, FIXED_QUALITY, FIXED_EXTENSION);
            }

            // upload request
            PutObjectRequest putObjRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(FIXED_CONTENT_TYPE)
                    .build();

            // upload lên r2 bằng mảng các bytes
            s3Client.putObject(
                    putObjRequest,
                    RequestBody.fromBytes(optimizedImageBytes)
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
    public List<String> uploadMultiTmpFiles(@NonNull List<MultipartFile> files) {

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
    // update batch delete do aws sdk support
    public void deleteFilesWithPrefix(String prefix, Instant thresholdTime) {

        // request lấy ds trong prefix
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        // Gọi r2 lấy ds, nếu quá dài thì auto phân trang
        ListObjectsV2Iterable paginator = s3Client.listObjectsV2Paginator(listRequest);

        // số luọng file đã xoá
        int deletedCount = 0;
        // Kích thước tổng của các files đã xoá
        long totalSize = 0;

        // gom các key lại vào đây rồi sau xoá hàng loạt với 1 lần call api duy nhất
        // ko call nhiều lần bằng method deleteFile nữa
        List<ObjectIdentifier> keysToDelete = new ArrayList<>();

        // Duyệt qua ds
        for (S3Object s3Object : paginator.contents()) {

            // file nào đc tạo ta với thoigian trc thoigian 3 tiếng trc
            // tức là quá 3 tiếng kể từ lúc file đó được up lên r2 thì dọn luôn
            if (s3Object.lastModified().isBefore(thresholdTime)) {

                // add lần lượt key của các files muốn xoá
                keysToDelete.add(ObjectIdentifier.builder()
                        .key(s3Object.key())
                        .build());
                log.info("Gom key {}", s3Object.key());

                totalSize += s3Object.size();
                deletedCount++;

                // Nếu gom được 1000 files thì xoá xoá hàng loạt luôn
                // vì aws cho phép max là xoá 1000 files mỗi lượt
                if (deletedCount == 1000) {
                    executeBatchDelete(keysToDelete); // gửi request xoá hàng loạt
                    keysToDelete.clear(); // clear ds keys đi
                }
            }
        }

        // Nếu ko gom đủ 1000 files thì cũng xoá hàng loạt
        // những files đã gom luôn
        if (!keysToDelete.isEmpty()) {
            executeBatchDelete(keysToDelete);
        }

        log.info("Đã xoá {} files, tổng kích thước là {} bytes", deletedCount, totalSize);
    }

    // Hàm giúp gửi request delete hàng loạt
    public void executeBatchDelete(List<ObjectIdentifier> keysToDelete) {

        DeleteObjectsRequest deleteObjsRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete -> delete.objects(keysToDelete))
                .build();

        s3Client.deleteObjects(deleteObjsRequest);
    }

    // Hàm giúp optimize ảnh (width, height, quality, extension)
    public byte[] optimizeImage(MultipartFile file, int targetWidth, float quality, String extension) throws IOException {

        // hứng data sau khi nén
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        // xử lý file ảnh (trong qtrình này các thứ như metadata auto fall giúp giảm size)
        Thumbnails.of(file.getInputStream())
                // chỉ cần set width, height sẽ tự scale theo tỷ lệ để ảnh ko bóp
                .width(targetWidth)
                // chất lg
                .outputQuality(quality)
                // ép đầu ra thành định dạng file nào đó
                .outputFormat(extension)
                // đổ vào cái hứng data
                .toOutputStream(os);

        return os.toByteArray();
    }

    public String generatePublicUrl(String key) {
        return publicUrl + "/" + key;
    }

}
