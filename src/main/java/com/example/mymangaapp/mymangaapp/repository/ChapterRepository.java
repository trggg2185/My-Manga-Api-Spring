package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, String> {

    // Check cùng 1 chapter không thể có 2 chapter index giống nhau
    boolean existsByMangaIdAndChapterIndex(String mangaId, Integer chapterIndex);
}
