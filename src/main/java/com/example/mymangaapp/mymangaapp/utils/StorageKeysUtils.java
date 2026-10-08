package com.example.mymangaapp.mymangaapp.utils;

import com.example.mymangaapp.mymangaapp.service.storage.ImageProcessor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class StorageKeysUtils {

    private StorageKeysUtils() {}

    static String TMP_PREFIX = "tmp/";
    static String AVATAR_PREFIX = "avatars/";
    static DateTimeFormatter DATE_FOLDER =  DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // url r2 cho các file ảnh đc lưu trong tmp: tmp/yyyy-MM-dd/uuid.webp
    public static String tmp() {
        return TMP_PREFIX + LocalDateTime.now(ZoneId.systemDefault()).format(DATE_FOLDER) + "/" + newFileName();
    }

    // url r2 cho avatar: avatars/userId/uuid.webp
    public static String avatar(String userId) {
        return AVATAR_PREFIX + userId + "/" + UUID.randomUUID() + "/" + newFileName();
    }

    public static boolean isTmp(String key) {
        return key.startsWith(TMP_PREFIX);
    }

    // tên file là uuid, đuôi file là webp
    public static String newFileName() {
        return UUID.randomUUID() + "." + ImageProcessor.OUTPUT_EXTENSION;
    }

}
