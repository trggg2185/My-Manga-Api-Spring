package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.Set;

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
import jakarta.persistence.ManyToOne;
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
// Spring Data JPA gọi là Auditing giúp ghi lại: ngày tạo, ngày cập nhật, ai
// tạo, ai cập nhật
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User {

    /*
     * - Lazy fetch là mặc định trong các annotation sau: OneToMany, ManyToMany
     * - Lazy eager mặc định trong: ManyToOne, OneToOne
     * => nên lazy fetch hết cho tao
     * 
     * - Entity không nên validate mà nên validate ở request
     */

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // username không phân biệt chữ hoa thường
    @Column(name = "username", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COLLATE utf8mb4_unicode_ci")
    String username;

    @Column(name = "email", unique = true)
    String email;

    @CreatedDate // JPA tự động gán ngày khởi tạo khi insert vào db
    @Column(name = "member_since", nullable = false, updatable = false)
    LocalDate memberSince;

    @Column(name = "password", nullable = false)
    String password;

    String facebook;
    String discord;

    @Column(name = "bio", length = 300)
    String bio;

    // QH: Nhiều user có thể thuộc về 1 trans group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transgroup_id") // chỉ định tên cột khoá ngoại sẽ sinh dưới db
    TransGroup transGroup;

    // QH: 1 user có thể có nhiểu role
    @ManyToMany(fetch = FetchType.LAZY)
    Set<Role> roles;

}
