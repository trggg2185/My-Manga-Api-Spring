package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageService {

    final S3Client s3Client;

    @Value("${cloud.r2.bucket}")
    String bucketName;

    @Value("${cloud.r2.s3.public-url}")
    String publicUrl;

    // Các đuôi file chấp nhận cho upload
    static final List<String> VALID_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");
    // đuôi các file sẽ upload lên r2
    static final String DEFAULT_EXTENSION = "webp";
    // content type
    static final String DEFAULT_CONTENT_TYPE = "image/webp";
    // width các file khi up lên r2 (1200px)
    static final int DEFAULT_TARGET_WIDTH = 1200;
    // kích thước avatar (vuông)
    static final int DEFAULT_AVATAR_SIZE = 256;
    // chất lg các file khi up lên r2 (mức 80% là tiêu chuẩn)
    static final float DEFAULT_QUALITY = 0.8f;
    // đây là kích thước tối đa của file sau khi optimize (1MB)
    static final long MAX_BYPASS_SIZE = 1024 * 1024;


    // ---------------------------chức năng dành cho admin và translator--------------------------- //

    // upload 1 file, yc có role translator
    @PreAuthorize("hasAnyRole('ADMIN', 'TRANSLATOR')")
    public String uploadTmpFile(@NonNull MultipartFile file) {
        try {
            String dateFolder = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String objectKey = buildObjectKey("tmp/" + dateFolder);
            byte[] optimizedImageBytes = prepareTmpUploadBytes(file);

            // upload lên và trả về url cho frontend truy cập để hiển thị
            return upload(objectKey, optimizedImageBytes);
        }
        catch (IOException exception) {
            throw new AppException(ResponseCode.FILE_UPLOAD_FAILED);
        }

    }

    // upload nhiều files
    @PreAuthorize("hasAnyRole('ADMIN', 'TRANSLATOR')")
    public List<String> uploadMultiTmpFiles(@NonNull List<MultipartFile> files) {

        // Thay vì dùng foreach lặp tuần tự mất thời gian
        // ta dùng luồng song song chạy nhanh hơn
        return files.parallelStream()
                .map(this::uploadTmpFile)
                .toList();

    }


    // ----------------------------- các method cho service khác dùng ----------------------------- //

    public String uploadAvatar(String userId, MultipartFile avatarFile) {
        try {
            String objectKey = buildObjectKey("avatars/" + userId);
            byte[] optimizedImageBytes = prepareAvatarUploadBytes(avatarFile);

            return upload(objectKey, optimizedImageBytes);
        }
        catch (IOException exception) {
            throw new AppException(ResponseCode.FILE_UPLOAD_FAILED);
        }
    }

    public String upload(String objectKey, byte[] optimizedImageBytes) {
        // upload request
        PutObjectRequest putObjRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(DEFAULT_CONTENT_TYPE)
                .build();

        // upload lên r2 bằng mảng các bytes
        s3Client.putObject(
                putObjRequest,
                RequestBody.fromBytes(optimizedImageBytes)
        );

        log.info("Object key: {}", objectKey);

        return generatePublicUrl(objectKey);
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
                if (keysToDelete.size() == 1000) {
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


    // -------------------------------------- method tiện ích ----------------------------------------//

    // Hàm giúp gửi request delete hàng loạt
    private void executeBatchDelete(List<ObjectIdentifier> keysToDelete) {

        DeleteObjectsRequest deleteObjsRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete -> delete.objects(keysToDelete))
                .build();

        s3Client.deleteObjects(deleteObjsRequest);
    }

    // Hàm giúp optimize ảnh (width, height, quality, extension)
    private byte[] optimizeImage(MultipartFile file) throws IOException {

        // hứng data sau khi nén
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        // xử lý file ảnh (trong qtrình này các thứ như metadata auto fall giúp giảm size)
        Thumbnails.of(file.getInputStream())
                // chỉ cần set width, height sẽ tự scale theo tỷ lệ để ảnh ko bóp
                .width(DEFAULT_TARGET_WIDTH)
                // chất lg
                .outputQuality(DEFAULT_QUALITY)
                // ép đầu ra thành định dạng file nào đó
                .outputFormat(DEFAULT_EXTENSION)
                // đổ vào cái hứng data
                .toOutputStream(os);

        return os.toByteArray();
    }

    private byte[] optimizeAvatarImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage sourceImage = readImage(file);
        int square = Math.min(sourceImage.getWidth(), sourceImage.getHeight());

        Thumbnails.of(file.getInputStream())
                .sourceRegion(Positions.CENTER, square, square)
                .size(DEFAULT_AVATAR_SIZE, DEFAULT_AVATAR_SIZE)
                .outputQuality(DEFAULT_QUALITY)
                .outputFormat(DEFAULT_EXTENSION)
                .toOutputStream(os);

        return os.toByteArray();
    }

    private byte[] prepareTmpUploadBytes(@NonNull MultipartFile file) throws IOException {
        String extension = extractAndValidateExtension(file);

        if (shouldBypassOptimization(file, extension)) {
            return file.getBytes();
        }

        return optimizeImage(file);
    }

    private byte[] prepareAvatarUploadBytes(@NonNull MultipartFile file) throws IOException {
        extractAndValidateExtension(file);
        return optimizeAvatarImage(file);
    }

    private BufferedImage readImage(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if (bufferedImage == null) {
            throw new AppException(ResponseCode.FILE_INVALID);
        }
        return bufferedImage;
    }

    private String extractAndValidateExtension(@NonNull MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(ResponseCode.FILE_REQUIRED);
        }

        String originalFileName = file.getOriginalFilename();
        String rawExtension = StringUtils.getFilenameExtension(originalFileName);

        if (rawExtension == null) {
            throw new AppException(ResponseCode.FILE_INVALID);
        }

        String extension = rawExtension.toLowerCase();
        if (!VALID_EXTENSIONS.contains(extension)) {
            throw new AppException(ResponseCode.FILE_INVALID);
        }

        return extension;
    }

    // check nên optimize cho file này ko
    // vd đã là file webp thì ko optimize thành file webp nữa, tránh duplicate
    private boolean shouldBypassOptimization(MultipartFile file, String extension) {
        return extension.equals(DEFAULT_EXTENSION) && file.getSize() <= MAX_BYPASS_SIZE;
    }

    private String buildObjectKey(String folderPath) {
        String filename = UUID.randomUUID() + "." + DEFAULT_EXTENSION;
        return folderPath + "/" + filename;
    }

    // tạo nhanh publicurl từ object key
    private String generatePublicUrl(String key) {
        return publicUrl + "/" + key;
    }

}
