package com.example.mymangaapp.mymangaapp.dto.request;

import jakarta.validation.constraints.Email;
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
public class UserUpdateRequest {

    // Update info user không nên cho update password trong này
    // Nên để đổi password thành 1 endpoint riêng

    @Email(message = "EMAIL_INVALID")
    String email;

    String facebook;
    String discord;

    @Size(max = 300, message = "BIO_INVALID")
    String bio;

}
