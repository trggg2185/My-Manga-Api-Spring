package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransGroupRepository extends JpaRepository<TransGroup, String> {

    boolean existsByName(String name);
}
