package com.example.mymangaapp.mymangaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mymangaapp.mymangaapp.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
}
