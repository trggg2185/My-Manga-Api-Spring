package com.example.mymangaapp.mymangaapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

// thay vì thêm 2 columns createdAt và updatedAt vào tất cả các entities
// thì ta chỉ cần tạo abstract class Base để các entities khác kế thừa các columns
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass // giúp jpa đây là ko phải table mà là class để các table kế thừa columns
@EntityListeners(AuditingEntityListener.class) // auto bắt event insert or update
@SuperBuilder
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

}
