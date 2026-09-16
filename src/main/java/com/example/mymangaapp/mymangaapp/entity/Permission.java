package com.example.mymangaapp.mymangaapp.entity;

import java.util.Set;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
public class Permission extends BaseEntity {

    @Id
    @Column(length = 50, nullable = false)
    String name;

    @Column(length = 100)
    String description;

    // QH: 1 permission có thể thuộc về nhiều role
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    Set<Role> roles;
}
