package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TransGroupCreationRequest {

    @NotBlank(message = "TRANSGROUP_NAME_REQUIRED")
    @Size(min = 5, max = 50, message = "TRANSGROUP_NAME_INVALID")
    String name;

}
