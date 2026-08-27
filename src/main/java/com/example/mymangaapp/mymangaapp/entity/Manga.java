package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.List;

import com.example.mymangaapp.mymangaapp.enums.MangaStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "published_date", nullable = false, updatable = false)
    LocalDate publishedDate;

    // QH: 1 manga có thể có nhiều trans group dịch
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "manga_transgroups",
        joinColumns = @JoinColumn(name = "manga_id"),
        inverseJoinColumns = @JoinColumn(name = "transgroup_id")
    )
    // Lưu dạng List chứ ko dùng Set
    List<TransGroup> transGroups;

    // QH: 1 manga có nhiều chapter
    @OneToMany(mappedBy = "manga", fetch = FetchType.LAZY)
    List<Chapter> chapters;

}
