package com.example.mymangaapp.mymangaapp.dto.chapter.response;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterSummaryResponse {

    // Chapterresponse trả về tối giản nhất
    String id;
    Integer chapterIndex;
    String title;
    Long views;
    Instant updatedAt;

}
