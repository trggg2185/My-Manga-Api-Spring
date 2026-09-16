package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChapterRequest {

    @NotBlank(message = "CHAPTER_INDEX_REQUIRED")
    @Positive(message = "CHAPTER_INDEX_POSITIVE")
    Integer chapterIndex;

    @Size(max = 200, message = "CHAPTER_TITLE_INVALID")
    String title;

    // Khi submit lưu chap thì frontend gửi 1 list các url của
    // các ảnh đã lưu ở trên r2 ở thư mục tmp
    @NotEmpty(message = "PAGE_URLS_REQUIRED")
    List<String> pageUrls;
}
