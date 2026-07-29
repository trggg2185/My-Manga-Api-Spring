package com.example.mymangaapp.mymangaapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.NonNull;

import com.example.mymangaapp.mymangaapp.dto.request.PermissionRequest;
import com.example.mymangaapp.mymangaapp.dto.response.PermissionResponse;
import com.example.mymangaapp.mymangaapp.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    
    @Mapping(target = "roles", ignore = true)
    @NonNull
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

}
