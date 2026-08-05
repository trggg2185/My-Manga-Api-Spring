package com.example.mymangaapp.mymangaapp.service;

import java.util.HashSet;
import java.util.List;

import com.example.mymangaapp.mymangaapp.utils.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.mymangaapp.mymangaapp.dto.request.RoleRequest;
import com.example.mymangaapp.mymangaapp.dto.response.RoleResponse;
import com.example.mymangaapp.mymangaapp.entity.Permission;
import com.example.mymangaapp.mymangaapp.entity.Role;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.RoleMapper;
import com.example.mymangaapp.mymangaapp.repository.PermissionRepository;
import com.example.mymangaapp.mymangaapp.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    RoleMapper roleMapper;

    public RoleResponse createRole(@NotNull RoleRequest request) {

        // Phải có role admin
        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        if (roleRepository.existsById(request.getName())) {
            throw new AppException(ResponseCode.ROLE_NAME_ALREADY_EXISTS);
        }

        if (CollectionUtils.isEmpty(request.getPermissions())) {
            throw new AppException(ResponseCode.PERMISSION_REQUIRED);
        }

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());

        log.info(permissions.toString());
        
        if (CollectionUtils.isEmpty(permissions)) {
            throw new AppException(ResponseCode.PERMISSION_INVALID);
        }

        Role role = roleMapper.toRole(request);
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAllRoles() {

        // Phải có role admin
        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(role -> 
                        roleMapper.toRoleResponse(role))
                .toList();
    }

    public void deleteRoleById(@NonNull String id) {

        // Phải có role admin
        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        if (!roleRepository.existsById(id)) {
            throw new AppException(ResponseCode.ROLE_NOT_FOUND);
        }

        roleRepository.deleteById(id);
    }

}
