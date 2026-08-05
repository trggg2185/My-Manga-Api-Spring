package com.example.mymangaapp.mymangaapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransGroupResponse {

    String id;
    UserResponse leader;
    String name;
    String status;
    Set<UserResponse> members;
    String description;
    LocalDate foundedDate;

}
