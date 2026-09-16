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

    @NotBlank
    @Size(max = 200, message = "MANGA_NAME_INVALID")
    String name;

    @Size(max = 255, message = "AUTHORS_NAME_INVALID")
    String authorsName;

    // Not blank dùng cho string, còn notempty dùng cho collection, set, list
    @NotEmpty(message = "CATEGORIES_REQUIRED")
    Set<String> categoryIds;

    @NotBlank(message = "MANGA_STATUS_REQUIRED")
    @Size(max = 20, message = "MANGA_STATUS_INVALID")
    String status;

    @Size(max = 500, message = "MANGA_DESCRIPTION_INVALID")
    String description;

}
