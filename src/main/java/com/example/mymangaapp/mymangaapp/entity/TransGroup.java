package com.example.mymangaapp.mymangaapp.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
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
public class TransGroup {
    
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
    List<User> members;

    @Column(name = "name", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    @Size(min = 3, message = "Tên nhóm dịch không được dưới 3 ký tự!")
    String name;

    // QH: 1 trans group có thể dịch nhiều manga
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "transGroups")
    List<Manga> mangas;

}
