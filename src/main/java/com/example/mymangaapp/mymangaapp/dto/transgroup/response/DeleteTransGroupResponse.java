package com.example.mymangaapp.mymangaapp.dto.transgroup.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteTransGroupResponse {

    String groupId;
}
