package com.example.mymangaapp.mymangaapp.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class Role {

    @Id
    String name; // name chính là id của role table

    String description;

    // QH: 1 role có thể thuộc về nhiều user
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    Set<User> users;

    // QH: 1 role có thể có nhiều permission
    @ManyToMany(fetch = FetchType.LAZY)
    Set<Permission> permissions;
}
