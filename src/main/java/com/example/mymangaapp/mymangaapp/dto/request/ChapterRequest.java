package com.example.mymangaapp.mymangaapp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChapterRequest {

    Integer chapterIndex;
    String title;

    // Khi submit lưu chap thì frontend gửi 1 list các url của
    // các ảnh đã lưu ở trên r2 ở thư mục tmp
    List<String> pageUrls;
}
