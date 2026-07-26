package com.example.mymangaapp.mymangaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mymangaapp.mymangaapp.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);
}
