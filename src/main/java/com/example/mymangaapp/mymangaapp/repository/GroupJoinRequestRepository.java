package com.example.mymangaapp.mymangaapp.repository;

import com.example.mymangaapp.mymangaapp.entity.GroupJoinRequest;
import com.example.mymangaapp.mymangaapp.enums.GroupJoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, String> {

    List<GroupJoinRequest> findAllByStatus(GroupJoinRequestStatus status);

}
