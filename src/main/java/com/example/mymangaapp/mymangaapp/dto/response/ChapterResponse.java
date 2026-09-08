package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {

    String id;
    String mangaName;
    Integer chapterIndex;
    String title;
    Long views;
    List<String> pageUrls;
    Instant updatedAt;

}
