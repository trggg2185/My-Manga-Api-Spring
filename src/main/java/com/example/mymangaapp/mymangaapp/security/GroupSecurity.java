package com.example.mymangaapp.mymangaapp.security;

import com.example.mymangaapp.mymangaapp.repository.TransGroupRepository;
import com.example.mymangaapp.mymangaapp.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Tạo security bean phục vụ cho custom phần quyền bằng annotation
@Slf4j
@Component("groupSec")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupSecurity {

    TransGroupRepository transGroupRepository;

    // Check role là leader nhóm dịch hay là admin
    public boolean isGroupLeaderOrAdmin(String groupId) {
        // là admin thì có quyền luôn
        if (SecurityUtils.isAdmin()) {
            return true;
        }

        return isGroupLeader(groupId);
    }

    // Check user hiện tại là leader nhóm này không
    public boolean isGroupLeader(String groupId) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        log.info("Current user id: {}", currentUserId);
        log.info("Group id: {}", groupId);

        return transGroupRepository.existsByIdAndLeaderId(groupId, currentUserId);
    }

}
