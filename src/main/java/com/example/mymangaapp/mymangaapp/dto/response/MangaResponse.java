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
public class MangaResponse {

    String id;
    String name;
    String authorsName;
    String genres;
    String status;
    List<String> transGroupsId;
    String description;
    LocalDate publishedDate;

}
