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

    @Size(max = 300, message = "TRANSGROUP_DESCRIPTION_INVALID")
    String name;

}
