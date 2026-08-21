package com.example.mymangaapp.mymangaapp.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mymangaapp.mymangaapp.entity.Role;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    @EntityGraph(attributePaths = { "permissions" })
    @NonNull
    Optional<Role> findById(@NonNull String id);

}
