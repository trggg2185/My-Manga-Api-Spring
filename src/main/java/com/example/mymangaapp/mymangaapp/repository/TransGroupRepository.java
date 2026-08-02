package com.example.mymangaapp.mymangaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mymangaapp.mymangaapp.entity.TransGroup;

public interface TransGroupRepository extends JpaRepository<TransGroup, String> {
    
}
