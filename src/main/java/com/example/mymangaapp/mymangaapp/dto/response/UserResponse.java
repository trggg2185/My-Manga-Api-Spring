package com.example.mymangaapp.mymangaapp.dto.response;

import java.time.Instant;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String id;
    String username;
    String email;
    String facebook;
    String discord;
    String bio;
    Set<RoleResponse> roles;
    String transGroupId;
    Instant createdAt;
    Instant updatedAt;
}
