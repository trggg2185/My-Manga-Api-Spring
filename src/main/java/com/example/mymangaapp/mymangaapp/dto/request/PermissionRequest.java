package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PermissionRequest {

    @NotBlank(message = "PERMISSION_NAME_REQUIRED")
    @Size(max = 50, message = "PERMISSION_NAME_INVALID")
    String name;

    @Size(max = 100, message = "PERMISSION_DESCRIPTION_INVALID")
    String description;

}
