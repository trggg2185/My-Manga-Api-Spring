package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransGroupRepository extends JpaRepository<TransGroup, String> {

    boolean existsByName(String name);

    // Khi gọi find all thì cố gắng load luôn leader và members để tránh lazy exception
    // nếu không khi map sang transgroup response thì mapper tự động gọi
    // getLeader và getMembers, khi jpa sẽ sinh sql nhưng session đã đóng
    // từ khi gọi find all rồi nên bắn lazy ngay
    @EntityGraph(attributePaths = { "leader", "leader.roles", "leader.roles.permissions", "members" })
    @NotNull
    List<TransGroup> findAll();

    @EntityGraph(attributePaths = { "leader", "leader.roles", "leader.roles.permissions", "members" })
    List<TransGroup> findAllByStatus(TransGroupStatus status);

    boolean existsByIdAndLeaderId(String id, String leaderId);
}
