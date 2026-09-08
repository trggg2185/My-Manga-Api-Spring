package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JoinRequestResponse {

    String id;
    String transGroupName;
    String username;
    String status;
    Instant createdAt;
    Instant updatedAt;

}
