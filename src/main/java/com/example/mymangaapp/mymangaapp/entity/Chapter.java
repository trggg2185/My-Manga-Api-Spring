package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
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
@Table(
        name = "chapter",
        uniqueConstraints = {
                // Set cặp id, index là duy nhất
                // tức là cùng 1 bộ manga không thể có 2 chapter có index giống nhau
                @UniqueConstraint(columnNames = { "manga_id", "chapter_index" })
        }
)
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

    // field này dùng để săpxep và tính toàn các chương, dạng số nguyên
    // Không đc đặt tên biến là index vi phạm quy tắc sql
    @Column(name = "chapter_index", nullable = false)
    Integer chapterIndex;

    // title là tiêu đề của chương này ví dụ "Sự khởi đầu"
    String title;

    @CreatedDate
    @Column(name = "published_date", nullable = false, updatable = false)
    LocalDate publishedDate;

    @LastModifiedDate
    @Column(name = "updatedDate", nullable = false)
    LocalDate updatedDate;

    // QH: 1 chapter chứa nhiều page (ảnh)
    // cascade giúp ko tự lưu chapter và lưu page thủ công nữa, chỉ cần lưu chapter auto lưu page trong chapter đó
    // orphanremoval giúp khi xoá chapter tự động xoá page thuộc về chapter đó
    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<Page> pages = new ArrayList<>(); // khởi tạo list rỗng tránh null pointer ex khi add
}
