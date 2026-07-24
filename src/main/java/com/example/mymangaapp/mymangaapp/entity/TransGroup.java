package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@EntityListeners(AuditingEntityListener.class)
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

    // name của trans group cũng ko pb hoa thường
    @Column(name = "name", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String name;

    // QH: 1 trans group có thể dịch nhiều manga
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "transGroups")
    List<Manga> mangas;

    @CreatedDate
    @Column(name = "founded_date", nullable = false, updatable = false)
    LocalDate foundedDate;

}
