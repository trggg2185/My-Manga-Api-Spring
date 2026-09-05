package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    LocalDate updatedDate;

}
