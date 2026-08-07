package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransGroupRepository extends JpaRepository<TransGroup, String> {

    boolean existsByName(String name);

    List<TransGroup> findAllByStatus(TransGroupStatus status);

    boolean existsByIdAndLeaderId(String id, String leaderId);
}
