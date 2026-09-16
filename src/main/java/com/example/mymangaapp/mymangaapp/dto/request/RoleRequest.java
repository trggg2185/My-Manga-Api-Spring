package com.example.mymangaapp.mymangaapp.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class RoleRequest {

    @NotBlank(message = "ROLE_NAME_REQUIRED")
    @Size(max = 20, message = "ROLE_NAME_INVALID")
    String name;

    @Size(max = 100, message = "ROLE_DESCRIPTION_INVALID")
    String description;

    @NotEmpty(message = "PERMISSIONS_REQUIRED")
    Set<String> permissions;

}
