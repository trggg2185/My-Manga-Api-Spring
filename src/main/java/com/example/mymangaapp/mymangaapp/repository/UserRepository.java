package com.example.mymangaapp.mymangaapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mymangaapp.mymangaapp.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Khi lấy user, lấy luôn roles, và khi lấy các roles, lấy luôn các permissions
    @EntityGraph(attributePaths = { "roles", "roles.permissions", "transGroup" })
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = { "roles", "roles.permissions" })
    @NonNull
    List<User> findAll();

    // Thao tác sẽ duyệt tất cả các user thuộc về nhóm dịch để set trường transgroup_id về null
    @Modifying // Cho truy vấn update/delete làm thay đổi dữ liệu trong db
    @Query("UPDATE User u SET u.transGroup = null WHERE u.transGroup.id = :transGroupId")
    void clearTransGroupFromMembers(@Param("transGroupId") String id);
}
