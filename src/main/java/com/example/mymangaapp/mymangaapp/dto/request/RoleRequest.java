package com.example.mymangaapp.mymangaapp.dto.request;

import java.util.Set;

import org.springframework.lang.NonNull;

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
    
    @NonNull
    String name;
    
    String description;

    @NonNull
    Set<String> permissions;

}
