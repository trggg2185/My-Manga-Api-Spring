package com.example.mymangaapp.mymangaapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mymangaapp.mymangaapp.dto.request.PermissionRequest;
import com.example.mymangaapp.mymangaapp.dto.response.PermissionResponse;
import com.example.mymangaapp.mymangaapp.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    
    @Mapping(target = "roles", ignore = true)
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

}
