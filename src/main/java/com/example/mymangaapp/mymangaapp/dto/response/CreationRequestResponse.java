package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreationRequestResponse {

    String id;
    String nameGroup;
    String creatorId;
    String description;
    Instant createdAt;
    Instant updatedAt;

}
