package com.example.mymangaapp.mymangaapp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Table(
        name = "page",
        uniqueConstraints = {
                // đảm bảo 1 chapter ko có 2 page trùng pageNumber
                @UniqueConstraint(columnNames = { "chapter_id", "page_number" })
        }
)
@Entity
public class Page extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // QH: nhiều page (ảnh) thuộc về 1 chapter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    Chapter chapter;

    // Field này để xác định trang này là
    // trang bao nhiêu trong 1 chap
    // có thể dùng để sx, thường được gán unique
    // và đánh index để tăng tốc độ tìmkiếm
    @Column(name = "page_number", nullable = false)
    Integer pageNumber;

    String imageUrl;
}
