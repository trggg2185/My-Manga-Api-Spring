package com.example.mymangaapp.mymangaapp.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

// Bảng các token đã bị vô hiệu hoá (tức là access token vẫn còn hiệu lực nhưng đã bị logout)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
public class InvalidatedToken extends BaseEntity {
    
    @Id
    String id;
    Instant expirationTime;

}
