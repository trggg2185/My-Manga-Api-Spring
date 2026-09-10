package com.example.mymangaapp.mymangaapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
// Thể loại của truyện
public class Category extends BaseEntity {

    // id của thể loại sẽ là long tăng dần
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    String name;

    @Column(length = 500)
    String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    Set<Manga> mangas;
}
