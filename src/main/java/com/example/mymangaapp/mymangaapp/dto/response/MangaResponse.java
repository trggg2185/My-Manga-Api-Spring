package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
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
    String ownerTransGroupId;
    List<String> transGroupsId;
    String description;
    Instant createdAt;

}
