package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MangaRequest {

    @Size(min = 3, message = "MANGA_NAME_INVALID")
    String name;

    @NotBlank(message = "AUTHORS_NAME_REQUIRED")
    String authorsName;

    // Not bland dùng cho string, còn notempty dùng cho collection, set, list
    @NotEmpty(message = "CATEGORIES_REQUIRED")
    Set<String> categoryIds;

    String status;

    String description;

}
