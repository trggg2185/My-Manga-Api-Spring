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
public class UserPasswordRequest {

    @NotBlank(message = "PASSWORD_REQUIRED")
    String oldPassword;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 5, message = "PASSWORD_INVALID")
    String newPassword;

}
