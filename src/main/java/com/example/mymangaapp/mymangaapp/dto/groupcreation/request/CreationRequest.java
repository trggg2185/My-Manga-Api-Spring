package com.example.mymangaapp.mymangaapp.dto.groupcreation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreationRequest {

    @NotBlank(message = "TRANSGROUP_NAME_REQUIRED")
    @Size(min = 5, max = 50, message = "TRANSGROUP_NAME_INVALID")
    String groupName;

    @Size(max = 300, message = "TRANSGROUP_DESCRIPTION_INVALID")
    String description;

}
