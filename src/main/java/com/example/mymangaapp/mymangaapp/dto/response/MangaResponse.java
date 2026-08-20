package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MangaResponse {

    String id;
    String name;
    String authorsName;
    String genres;
    String status;
    String description;
    LocalDate publishedDate;

}
