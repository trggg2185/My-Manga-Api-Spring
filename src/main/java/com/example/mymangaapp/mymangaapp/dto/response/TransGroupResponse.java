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
    // dùng response này của user cho gọn, ko select thêm role, permision, transgroup cho mệt
    UserSummaryResponse leader;
    String name;
    String status;
    Set<UserSummaryResponse> members;
    String description;
    LocalDate foundedDate;

}
