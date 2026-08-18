package com.example.mymangaapp.mymangaapp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MangaCreationRequest {

    String name;
    String authorsName;
    String genres;
    String status;
}
