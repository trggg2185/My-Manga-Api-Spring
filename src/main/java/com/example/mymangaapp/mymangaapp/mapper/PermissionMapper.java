package com.example.mymangaapp.mymangaapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mymangaapp.mymangaapp.dto.permission.request.PermissionRequest;
import com.example.mymangaapp.mymangaapp.dto.permission.response.PermissionResponse;
import com.example.mymangaapp.mymangaapp.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

}
