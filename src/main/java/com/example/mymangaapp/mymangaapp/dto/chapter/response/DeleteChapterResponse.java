package com.example.mymangaapp.mymangaapp.dto.chapter.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteChapterResponse {

    String mangaId;
    String chapterId;
}
