package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // QH: Nhiều chapter thuộc về 1 manga thôi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    Manga manga;

    // Khởi tạo views mặc định là 0, phải có builder default không builder sẽ ignore view mình gán mất
    @Builder.Default
    Long views = 0L;

    String name;
    String title;

    @CreatedDate
    @Column(name = "published_date", nullable = false, updatable = false)
    LocalDate publishedDate;

    @LastModifiedDate
    @Column(name = "updatedDate", nullable = false)
    LocalDate updatedDate;

    // QH: 1 chapter chứa nhiều page (ảnh)
    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY)
    List<Page> pages;
}
