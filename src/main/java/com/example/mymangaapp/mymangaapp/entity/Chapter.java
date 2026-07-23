package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // QH: Nhiều chapter thuộc về 1 manga thôi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    Manga manga;

    String name;
    String title;
    LocalDate publishedDate;

    // QH: 1 chapter chứa nhiều page (ảnh)
    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY)
    List<Page> pages;
}
