package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.response.JoinRequestResponse;
import com.example.mymangaapp.mymangaapp.entity.GroupJoinRequest;
import com.example.mymangaapp.mymangaapp.entity.Role;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.enums.GroupJoinRequestStatus;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.GroupJoinRequestMapper;
import com.example.mymangaapp.mymangaapp.repository.GroupJoinRequestRepository;
import com.example.mymangaapp.mymangaapp.repository.RoleRepository;
import com.example.mymangaapp.mymangaapp.repository.TransGroupRepository;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;
import com.example.mymangaapp.mymangaapp.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GroupJoinRequestService {

    RoleRepository roleRepository;
    UserRepository userRepository;
    TransGroupRepository transGroupRepository;
    GroupJoinRequestRepository groupJoinRequestRepository;

    GroupJoinRequestMapper groupJoinRequestMapper;

    @Transactional
    public JoinRequestResponse requestJoinGroup(@NonNull String groupId) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findWithDetailsByUsername(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        // Check xem user này đã vào nhóm dịch nào chưa
        if (user.getTransGroup() != null) {
            throw new AppException(ResponseCode.USER_ALREADY_IN_GROUP);
        }

        // Check nhóm tồn tại và đi vào hoạt động chưa
        TransGroup transGroup = transGroupRepository
                .findByIdAndStatus(groupId, TransGroupStatus.APPROVED)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        GroupJoinRequest groupJoinRequest = GroupJoinRequest
                .builder()
                .transGroup(transGroup)
                .user(user)
                .build();

        return groupJoinRequestMapper.toJoinRequestResponse(groupJoinRequestRepository.save(groupJoinRequest));

    }

    // Lấy các yêu cầu xin vào nhóm (chỉ leader của nhóm mới được phép)
    @PreAuthorize("@groupSec.isGroupLeader(#groupId)")
    public List<JoinRequestResponse> getJoinRequests(GroupJoinRequestStatus status, @NonNull String groupId) {

        if (status != null) {
            return groupJoinRequestRepository
                    .findAllByStatus(status)
                    .stream()
                    .map(groupJoinRequestMapper::toJoinRequestResponse)
                    .toList();
        }

        return groupJoinRequestRepository
                .findAll()
                .stream()
                .map(groupJoinRequestMapper::toJoinRequestResponse)
                .toList();

    }

    // Leader chấp nhận yêu cầu từ user xin vào nhóm
    @PreAuthorize("@groupSec.isGroupLeader(#groupId)")
    @Transactional
    public JoinRequestResponse approveJoinGroup(@NonNull String groupId, @NonNull String requestId) {

        GroupJoinRequest groupJoinRequest = groupJoinRequestRepository
                .findWithDetailsById(requestId)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_JOIN_REQUEST_NOT_FOUND));

        TransGroup transGroup = groupJoinRequest.getTransGroup();
        User user = groupJoinRequest.getUser();

        // Check xem request này có thực sự của của nhóm leader này không
        // Tránh việc leader gửi có kèm groupId vượt preauthorize
        // nhưng lại điền request id của nhóm khác để duyệt trộm
        if (!transGroup.getId().equals(groupId)) {
            throw new AppException(ResponseCode.GROUP_JOIN_REQUEST_INVALID);
        }

        // Check xem yêu cầu này có thật đang đợi duyệt không
        if (!groupJoinRequest.getStatus().equals(GroupJoinRequestStatus.PENDING)) {
            throw new AppException(ResponseCode.GROUP_JOIN_REQUEST_STATUS_INVALID);
        }
        groupJoinRequest.setStatus(GroupJoinRequestStatus.APPROVED);

        // Vẫn phải check user có group chưa
        // Nhỡ đâu trong khi đợi nhóm này duyệt, user vào nhóm khác mất rồi
        if (user.getTransGroup() != null) {
            throw new AppException(ResponseCode.USER_ALREADY_IN_GROUP);
        }

        Role translatorRole = roleRepository
                .findById("TRANSLATOR")
                .orElseThrow(() -> new AppException(ResponseCode.ROLE_NOT_FOUND));

        // Gán nhóm đó vào user
        user.setTransGroup(transGroup);
        // Gán role translator cho user
        user.getRoles().add(translatorRole);

        // Gán user đó vào danh sách member của nhóm
        transGroup.getMembers().add(user);

        userRepository.save(user);
        transGroupRepository.save(transGroup);

        return groupJoinRequestMapper.toJoinRequestResponse(
                groupJoinRequestRepository.save(groupJoinRequest)
        );
    }

}
