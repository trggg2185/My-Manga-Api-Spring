package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CategoryResponse {

    Long id;
    String name;
    String description;

}
