package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TransGroupCreationRequest {

    @Size(min = 3, message = "TRANSGROUP_NAME_INVALID")
    String name;

}
