package com.example.mymangaapp.mymangaapp.entity;

import java.util.List;
import java.util.Set;

import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
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
@Entity
public class TransGroup extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // QH: 1 trans group thì chỉ có 1 leader
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    User leader;

    // QH: 1 trans group có thể có nhiều thành viên
    // Báo cho JPA biết quan hệ này đã được quản lý bởi biến nào ở entity kia.
    // Bên có mappedBy sẽ KHÔNG tự sinh ra khóa ngoại.
    // Mặc định OneToMany đã có fetch lazy rồi nhưng thêm vào để cho rõ nghĩa
    @OneToMany(mappedBy = "transGroup", fetch = FetchType.LAZY)
    Set<User> members;

    // name của trans group cũng ko pb hoa thường
    @Column(name = "name", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    @Column(name = "description", length = 300)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    @Builder.Default
    TransGroupStatus status = TransGroupStatus.PENDING;

    // QH: 1 trans group có thể dịch nhiều manga
    // đây chỉ là ds manga mà nhóm dịch, chưa chắc đã sở hữu (tạo ra), có thể chỉ là dịch phụ
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "transGroups")
    Set<Manga> translatedMangas;

    // 1 transgroup sẽ sở hữu (tạo ra) nhiều manga
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ownerTransGroup")
    List<Manga> ownedMangas;

}
