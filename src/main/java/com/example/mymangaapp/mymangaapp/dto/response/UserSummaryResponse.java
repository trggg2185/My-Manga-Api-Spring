package com.example.mymangaapp.mymangaapp.dto.response;

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
    String email;
    String facebook;
    String discord;
    String bio;
    Instant createdAt;

}
