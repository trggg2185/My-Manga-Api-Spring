package com.example.mymangaapp.mymangaapp.service.storage;

import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

// Class này chỉ chuyên xử lý ảnh
@Component
public class ImageProcessor {

    public static final String OUTPUT_EXTENSION = "webp";
    public static final String OUTPUT_CONTENT_TYPE = "image/webp";

    private static final Set<String> VALID_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final int MAX_WIDTH = 1200;
    private static final int AVATAR_SIZE = 256;
    private static final float QUALITY = 0.8f;
    private static final long BYPASS_MAX_SIZE = 1024 * 1024;   // webp <= 1MB thì giữ nguyên
    private static final long MAX_PIXELS = 40_000_000L;

    /** Ảnh trang truyện: giới hạn chiều rộng 1200px, đổi sang webp. */
    public byte[] optimize(MultipartFile file) throws IOException {
        String extension = validate(file);

        if (OUTPUT_EXTENSION.equals(extension) && file.getSize() <= BYPASS_MAX_SIZE) {
            byte[] bytes = file.getBytes();
            if (!looksLikeWebp(bytes)) throw new AppException(ResponseCode.FILE_INVALID);
            return bytes;
        }

        BufferedImage source = readImage(file);
        Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(source);

        // Ảnh nhỏ hơn 1200px thì giữ nguyên kích thước, không phóng to
        builder = source.getWidth() > MAX_WIDTH ? builder.width(MAX_WIDTH) : builder.scale(1.0);

        return encode(builder);
    }

    /** Avatar: cắt vuông ở giữa, 256x256. */
    public byte[] toAvatar(MultipartFile file) throws IOException {
        validate(file);

        BufferedImage source = readImage(file);
        int side = Math.min(source.getWidth(), source.getHeight());

        return encode(Thumbnails.of(source)
                .sourceRegion(Positions.CENTER, side, side)
                .size(AVATAR_SIZE, AVATAR_SIZE));
    }

    private byte[] encode(Thumbnails.Builder<BufferedImage> builder) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        builder.outputQuality(QUALITY).outputFormat(OUTPUT_EXTENSION).toOutputStream(os);
        return os.toByteArray();
    }

    private String validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ResponseCode.FILE_REQUIRED);
        }
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !VALID_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new AppException(ResponseCode.FILE_INVALID);
        }
        return extension.toLowerCase();
    }

    // Đọc kích thước TRƯỚC khi giải nén toàn bộ ảnh, đồng thời xác nhận đây đúng là file ảnh
    private BufferedImage readImage(MultipartFile file) throws IOException {
        try (ImageInputStream in = ImageIO.createImageInputStream(file.getInputStream())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (!readers.hasNext()) throw new AppException(ResponseCode.FILE_INVALID);

            ImageReader reader = readers.next();
            try {
                reader.setInput(in);
                long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
                if (pixels > MAX_PIXELS) throw new AppException(ResponseCode.FILE_SIZE_INVALID);
                return reader.read(0);
            } finally {
                reader.dispose();
            }
        }
    }

    // WebP là container RIFF: "RIFF" + 4 byte kích thước + "WEBP"
    private boolean looksLikeWebp(byte[] b) {
        return b.length > 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
    }
}
