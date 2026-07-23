package com.example.mymangaapp.mymangaapp.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
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
public class User {

    /*
        Lazy fetch là mặc định trong các annotation sau: OneToMany, ManyToMany
        Lazy eager mặc định trong: ManyToOne, OneToOne
        => nên lazy fetch hết cho tao
    */

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    @Size(min = 3, message = "Tên người dùng không được dưới 3 ký tự!")
    String username;

    @Size(min = 5, message = "Mật khẩu không được dưới 5 ký tự!")
    String password;

    @Email
    @Column(name = "email", unique = true)
    String email;

    LocalDate memberSince;
    String facebook;
    String discord;

    @Column(length = 300)
    @Size(max = 300, message = "Thông tin bản thân không quá 300 ký tự!")
    String bio;

    // QH: Nhiều user có thể thuộc về 1 trans group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transgroup_id", nullable = true) // chỉ định tên cột khoá ngoại sẽ sinh dưới db
    TransGroup transGroup;

    // QH: 1 user có thể có nhiểu role
    @ManyToMany(fetch = FetchType.LAZY)
    Set<Role> roles;

}
