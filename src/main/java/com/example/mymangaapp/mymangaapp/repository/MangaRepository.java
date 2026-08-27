package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.enums.MangaStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface MangaRepository extends JpaRepository<Manga, String> {

    @EntityGraph(attributePaths = { "transGroups" })
    @NonNull
    List<Manga> findAll();

    @EntityGraph(attributePaths = { "transGroups"})
    List<Manga> findAllByStatus(MangaStatus status);

    @EntityGraph(attributePaths = { "transGroups" })
    @NonNull
    Optional<Manga> findById(@NonNull String id);

}
