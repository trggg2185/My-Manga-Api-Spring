package com.example.mymangaapp.mymangaapp.dto.user.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSummaryResponse {

    String id;
    String username;
    String avatar;
    Instant createdAt;

}
