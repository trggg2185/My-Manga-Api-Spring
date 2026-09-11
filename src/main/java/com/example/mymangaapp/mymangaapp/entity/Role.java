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
public class Role extends BaseEntity {

    @Id
    String name; // name chính là id của role table

    String description;

    // QH: 1 role có thể thuộc về nhiều user
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    Set<User> users;

    // QH: 1 role có thể có nhiều permission
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_name"),
            inverseJoinColumns = @JoinColumn(name = "permission_name")
    )
    Set<Permission> permissions;
}
