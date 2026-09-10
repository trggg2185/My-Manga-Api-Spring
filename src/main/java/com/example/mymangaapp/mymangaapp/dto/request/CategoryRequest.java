package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CategoryRequest {

    @NotBlank(message = "CATEGORY_NAME_REQUIRED")
    @Size(min = 2, max = 100, message = "CATEGORY_NAME_INVALID")
    String name;

    @Size(max = 500)
    String description;
}
