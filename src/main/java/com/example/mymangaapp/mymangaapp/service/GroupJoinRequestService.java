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
import com.example.mymangaapp.mymangaapp.security.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    // ---------------------------------------chức năng cho user đã đăng nhập ---------------------------------------//

    @Transactional
    public JoinRequestResponse requestJoinGroup(@NonNull String groupId) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findWithDetailsByUsername(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        // Check xem user này đã gửi yêu cầu đang chờ duyệt vào nhóm dịch này chưa, gửi lại thì báo lỗi
        if (groupJoinRequestRepository.existsByTransGroupIdAndUserIdAndStatus(groupId, user.getId(), GroupJoinRequestStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_JOIN_REQUEST_ALREADY_EXISTED);
        }

        // check user là member hay leader của nhóm dịch nào chưa
        if (user.getTransGroup() != null || transGroupRepository.existsByLeaderId(user.getId())) {
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

    // Hàm lấy những yêu cầu tham gia nhóm từ chính user hiện tại đang đăng nhập
    public List<JoinRequestResponse> getMyJoinRequests(GroupJoinRequestStatus status) {

        String currentUserId = SecurityUtils.getCurrentUserId();

        if (status != null) {
            return groupJoinRequestRepository
                    .findAllByUserIdAndStatus(currentUserId, status)
                    .stream()
                    .map(groupJoinRequestMapper::toJoinRequestResponse)
                    .toList();
        }

        return groupJoinRequestRepository
                .findAllByUserId(currentUserId)
                .stream()
                .map(groupJoinRequestMapper::toJoinRequestResponse)
                .toList();
    }


    // ---------------------------------------chức năng cho leader nhóm dịch ---------------------------------------//

    // Lấy các yêu cầu xin vào nhóm (chỉ leader của nhóm mới được phép)
    @PreAuthorize("@groupSec.isGroupLeader(#groupId)")
    public List<JoinRequestResponse> getJoinRequests(GroupJoinRequestStatus status, @NonNull String groupId) {

        if (status != null) {
            return groupJoinRequestRepository
                    .findAllByTransGroupIdAndStatus(groupId, status)
                    .stream()
                    .map(groupJoinRequestMapper::toJoinRequestResponse)
                    .toList();
        }

        return groupJoinRequestRepository
                .findAllByTransGroupId(groupId)
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
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        // check trong khoảng tg duyệt vào nhóm, user vào nhóm khác chưa
        if (user.getTransGroup() != null) {
            throw new AppException(ResponseCode.USER_ALREADY_IN_GROUP);
        }

        // Check xem yêu cầu này có thật đang đợi duyệt không
        if (!groupJoinRequest.getStatus().equals(GroupJoinRequestStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_JOIN_REQUEST_STATUS_INVALID);
        }
        groupJoinRequest.setStatus(GroupJoinRequestStatus.APPROVED);

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

    @PreAuthorize("@groupSec.isGroupLeader(#groupId)")
    @Transactional
    public JoinRequestResponse rejectJoinGroup(@NonNull String groupId, @NonNull String requestId) {

        GroupJoinRequest groupJoinRequest = groupJoinRequestRepository
                .findWithDetailsById(requestId)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_JOIN_REQUEST_NOT_FOUND));

        TransGroup transGroup = groupJoinRequest.getTransGroup();

        // Check xem request này có thực sự của của nhóm leader này không
        // Tránh việc leader gửi có kèm groupId vượt preauthorize
        // nhưng lại điền request id của nhóm khác để duyệt trộm
        if (!transGroup.getId().equals(groupId)) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        // Check xem yêu cầu này có thật đang đợi duyệt không
        if (!groupJoinRequest.getStatus().equals(GroupJoinRequestStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_JOIN_REQUEST_STATUS_INVALID);
        }
        groupJoinRequest.setStatus(GroupJoinRequestStatus.REJECTED);

        return groupJoinRequestMapper.toJoinRequestResponse(
                groupJoinRequestRepository.save(groupJoinRequest)
        );
    }

}
