package com.example.mymangaapp.mymangaapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mymangaapp.mymangaapp.dto.request.RoleRequest;
import com.example.mymangaapp.mymangaapp.dto.response.RoleResponse;
import com.example.mymangaapp.mymangaapp.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    @Mapping(target = "users", ignore = true)
    // Việc set các permission ta sẽ tự làm bên role service để chuyển set string thành set permission
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

}
