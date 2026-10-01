package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterRequest {

    @NotBlank(message = "USERNAME_REQUIRED")
    @Size(min = 3, max = 50, message = "USERNAME_INVALID")
    String username;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 5, max = 30, message = "PASSWORD_INVALID")
    String password;

    @Email(message = "EMAIL_INVALID")
    String email;
}
