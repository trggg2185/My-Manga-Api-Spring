package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.GroupJoinRequest;
import com.example.mymangaapp.mymangaapp.enums.GroupJoinRequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, String> {

    @EntityGraph(attributePaths = { "transGroup", "user" })
    List<GroupJoinRequest> findAllByStatus(GroupJoinRequestStatus status);

    @EntityGraph(attributePaths = { "transGroup", "user" })
    @NonNull
    Optional<GroupJoinRequest> findWithDetailsById(@NonNull String id);

}
