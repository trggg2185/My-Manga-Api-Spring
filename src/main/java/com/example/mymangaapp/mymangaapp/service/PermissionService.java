package com.example.mymangaapp.mymangaapp.service;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.mymangaapp.mymangaapp.dto.request.PermissionRequest;
import com.example.mymangaapp.mymangaapp.dto.response.PermissionResponse;
import com.example.mymangaapp.mymangaapp.entity.Permission;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.PermissionMapper;
import com.example.mymangaapp.mymangaapp.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    
    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(@NotNull PermissionRequest request) {

        if (permissionRepository.existsById(request.getName())) {
            throw new AppException(ResponseCode.PERMISSION_NAME_ALREADY_EXISTS);
        }

        Permission permission = permissionMapper.toPermission(request);

        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));

    }

    public List<PermissionResponse> getAllPermissions() {

        List<Permission> permissions = permissionRepository.findAll();

        return permissions
                .stream()
                .map(permission ->
                        permissionMapper.toPermissionResponse(permission))
                .toList();
    }

    public void deletePermissionById(@NonNull String id) {

        if (!permissionRepository.existsById(id)) {
            throw new AppException(ResponseCode.PERMISSION_NOT_FOUND);
        }

        permissionRepository.deleteById(id);
    }

}
