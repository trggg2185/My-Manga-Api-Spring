package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MangaCreationRequest {

    @Size(min = 3, message = "MANGA_NAME_INVALID")
    String name;

    @NotBlank(message = "AUTHORS_NAME_REQUIRED")
    String authorsName;

    @NotBlank(message = "GENRES_REQUIRED")
    String genres;

    String status;

    String description;

}
