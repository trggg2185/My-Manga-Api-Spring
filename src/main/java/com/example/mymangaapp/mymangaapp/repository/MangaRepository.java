package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.enums.MangaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface MangaRepository extends JpaRepository<Manga, String> {

    @EntityGraph(attributePaths = { "ownerTransGroup", "transGroups" })
    @NonNull
    Page<Manga> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = { "ownerTransGroup", "transGroups"})
    Page<Manga> findAllByStatus(MangaStatus status, Pageable pageable);

    @EntityGraph(attributePaths = { "ownerTransGroup", "transGroups" })
    @NonNull
    Optional<Manga> findById(@NonNull String id);

    @EntityGraph(attributePaths = { "chapters" })
    Optional<Manga> findWithChaptersById(String id);

    Page<Manga> findAllByTransGroupsId(String groupId, Pageable pageable);

}
