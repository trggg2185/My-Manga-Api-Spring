package com.example.mymangaapp.mymangaapp.dto.manga.response;

import com.example.mymangaapp.mymangaapp.dto.category.response.CategoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MangaSummaryResponse {

    String id;
    String name;
    String slug;
    String status;
    String description;
    Set<CategoryResponse> categories;
    String ownerTransGroupId;
    List<String> transGroupsId;
    Instant updatedAt;

}
