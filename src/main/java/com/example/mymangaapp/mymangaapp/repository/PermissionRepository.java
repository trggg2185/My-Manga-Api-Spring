package com.example.mymangaapp.mymangaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mymangaapp.mymangaapp.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
