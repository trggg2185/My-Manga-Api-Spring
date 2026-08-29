package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {

    String mangaName;
    String name;
    String title;
    Long views;
    LocalDate publishedDate;
    LocalDate updatedDate;
    List<String> pageUrls;

}
