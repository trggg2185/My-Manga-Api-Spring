package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
