package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.example.mymangaapp.mymangaapp.enums.MangaStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class Manga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String authorsName;
    String genres;

    // Lưu enum dưới dạng chuỗi (varchar) thay vì số 0, 1, 2
    @Enumerated(EnumType.STRING)
    // Set value mặc định vì dùng JPA tạo, cập nhật bảng
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ONGOING'")
    // Để Lombok ko bỏ qua giá trị khởi tạo = MangaStatus.ONGOING khi dựng object bằng Builder
    @Builder.Default
    MangaStatus status = MangaStatus.ONGOING;

    String description;
    Integer view;
    LocalDate publishedDate;

    // QH: 1 manga có thể có nhiều trans group dịch
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "manga_transgroups",
        joinColumns = @JoinColumn(name = "manga_id"),
        inverseJoinColumns = @JoinColumn(name = "transgroup_id")
    )
    Set<TransGroup> transGroups;

    // QH: 1 manga có nhiều chapter
    @OneToMany(mappedBy = "manga", fetch = FetchType.LAZY)
    List<Chapter> chapters;

}
