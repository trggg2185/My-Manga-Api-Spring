package com.example.mymangaapp.mymangaapp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Optional<User> findByUsername(String username);

    // Khi lấy user, lấy luôn roles, và khi lấy các roles, lấy luôn các permissions
    @EntityGraph(attributePaths = { "roles", "roles.permissions", "transGroup" })
    Optional<User> findWithDetailsByUsername(@NonNull String username);

    @EntityGraph(attributePaths = { "roles", "roles.permissions", "transGroup" })
    Optional<User> findWithDetailsById(@NonNull String id);

    @EntityGraph(attributePaths = { "roles", "roles.permissions", "transGroup" })
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

    // Thao tác sẽ duyệt tất cả các user thuộc về nhóm dịch để set trường transgroup_id về null
    @Modifying // Cho truy vấn update/delete làm thay đổi dữ liệu trong db
    @Query("UPDATE User u SET u.transGroup = null WHERE u.transGroup.id = :transGroupId")
    void clearTransGroupFromMembers(@Param("transGroupId") String id);

    // Method này cần sửa lại ----------------------------------------
    // Câu sql thuần để xoá role translator ra khỏi các members hiện tại trong 1 trans group
    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE users_id IN " +
                   "(SELECT id FROM user WHERE transgroup_id = :transGroupId) " +
                   "AND roles_name = 'TRANSLATOR'", nativeQuery = true)
    void removeTranslatorRoleFromMembers(@Param("transGroupId") String id);
}
