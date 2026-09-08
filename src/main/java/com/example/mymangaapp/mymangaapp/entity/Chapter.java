package com.example.mymangaapp.mymangaapp.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Table(
        name = "chapter",
        uniqueConstraints = {
                // Set cặp id, index là duy nhất
                // tức là cùng 1 bộ manga không thể có 2 chapter có index giống nhau
                @UniqueConstraint(columnNames = { "manga_id", "chapter_index" })
        }
)
@Entity
public class Chapter extends BaseEntity {

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

    // QH: 1 chapter chứa nhiều page (ảnh)
    // cascade giúp ko tự lưu chapter và lưu page thủ công nữa, chỉ cần lưu chapter auto lưu page trong chapter đó
    // orphanremoval giúp khi xoá chapter tự động xoá page thuộc về chapter đó
    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("pageNumber ASC") // luôn lấy các pages theo pageNumber tăng dần
    @Builder.Default
    List<Page> pages = new ArrayList<>(); // khởi tạo list rỗng tránh null pointer ex khi add
}
