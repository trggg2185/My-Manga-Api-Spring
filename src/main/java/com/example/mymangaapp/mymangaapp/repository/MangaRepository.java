package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.enums.MangaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

/* Quy định của repository:
*  - Public endpoints nên dùng method light / tối giản nếu không cần join nhiều bảng.
*  - Admin endpoints nên dùng method detailed / có entityGraph để join thêm thông tin cần thiết.
*  - Khi query liên quan đến membership group, phải tính cả ownerTransGroup và transGroups.
* */
public interface MangaRepository extends JpaRepository<Manga, String> {

   // Public list: trả về list tối giản, không join quá sâu
   @NonNull
   Page<Manga> findAll(@NonNull Pageable pageable);

   // Admin list: join thêm ownerTransGroup / transGroups / categories để view dashboard chi tiết hơn
   @EntityGraph(attributePaths = { "ownerTransGroup" })
   @Query("SELECT m FROM Manga m")
   Page<Manga> findAllDetailed(Pageable pageable);

   @EntityGraph(attributePaths = { "ownerTransGroup" })
   @Query("SELECT m FROM Manga m WHERE m.status = :status")
   Page<Manga> findAllDetailedByStatus(MangaStatus status, Pageable pageable);

   @EntityGraph(attributePaths = { "ownerTransGroup", "categories", "transGroups", "chapters" })
   Optional<Manga> findWithChaptersById(String id);

   @EntityGraph(attributePaths = { "ownerTransGroup", "categories", "transGroups" })
   Optional<Manga> findWithDetailsById(String id);

   // Group membership: tìm mọi manga mà group tham gia, bao gồm sỡ hữu và dịch phụ
   @EntityGraph(attributePaths = { "ownerTransGroup", "transGroups", "categories" })
   @Query(
       "SELECT DISTINCT m FROM Manga m " +
       "LEFT JOIN m.transGroups tg " +
       "WHERE m.ownerTransGroup.id = :groupId OR tg.id = :groupId"
   )
   Page<Manga> findAllByGroupMembershipId(@Param("groupId") String groupId, Pageable pageable);

}
