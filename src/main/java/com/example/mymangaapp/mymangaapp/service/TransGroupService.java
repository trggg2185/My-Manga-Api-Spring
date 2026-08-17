package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.request.TransGroupCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.TransGroupMapper;
import com.example.mymangaapp.mymangaapp.repository.TransGroupRepository;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;
import com.example.mymangaapp.mymangaapp.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransGroupService {

    UserRepository userRepository;
    TransGroupRepository transGroupRepository;

    TransGroupMapper transGroupMapper;

    @Transactional // Giúp không đóng hibernate session khi save, để không bị LazyException
    public TransGroupResponse requestCreateGroup(@NotNull TransGroupCreationRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        User leader = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        // Check người này đã có ở trong group chưa
        if (leader.getTransGroup() != null) {
            throw new AppException(ResponseCode.USER_ALREADY_IN_GROUP);
        }

        if (transGroupRepository.existsByName(request.getName())) {
            throw new AppException(ResponseCode.TRANSGROUP_NAME_ALREADY_EXISTS);
        }

        TransGroup transGroup = transGroupMapper.toTransGroup(request);
        transGroup.setLeader(leader);
        transGroup.setMembers(new HashSet<>());

        // Trưởng nhóm dịch cũng là 1 thành viên
        transGroup.getMembers().add(leader);

        // Nếu admin muốn tạo nhóm thì duyệt luôn
        if (SecurityUtils.isAdmin()) {
            transGroup.setStatus(TransGroupStatus.APPROVED);
        }

        transGroup = transGroupRepository.save(transGroup);

        // Cho user thuộc về nhóm dịch
        leader.setTransGroup(transGroup);
        userRepository.save(leader);

        // transgroup không save vì transactional có dirty checking tự update db
        return transGroupMapper.toTransGroupResponse(transGroup);
    }

    @Transactional
    public TransGroupResponse approveCreateGroup(@NonNull String id) {

        TransGroup transGroup = transGroupRepository
                .findWithDetailsById(id)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Duyệt nhóm tức là nhóm phải đang ở trạng thái chờ (PENDING) -> chấp nhận (APPROVED)
        if (!transGroup.getStatus().equals(TransGroupStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_STATUS_INVALID);
        }

        transGroup.setStatus(TransGroupStatus.APPROVED);

        // transgroup không save vì transactional có dirty checking tự update db
        return transGroupMapper.toTransGroupResponse(transGroup);
    }

    @Transactional
    public TransGroupResponse rejectCreateGroup(@NonNull String id) {

        TransGroup transGroup = transGroupRepository
                .findWithDetailsById(id)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Từ chối nhóm tức là nhóm phải đang ở trạng thái chờ (PENDING) -> từ chối (REJECTED)
        if (!transGroup.getStatus().equals(TransGroupStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_STATUS_INVALID);
        }

        transGroup.setStatus(TransGroupStatus.REJECTED);

        return transGroupMapper.toTransGroupResponse(transGroupRepository.save(transGroup));
    }

    // Lấy tất cả nhóm dịch đã được chấp thuận, public
    public List<TransGroupResponse> getGroups() {

        return transGroupRepository
                .findAllByStatus(TransGroupStatus.APPROVED)
                .stream()
                .map(transGroupMapper::toTransGroupResponse)
                .toList();
    }

    // Đây cũng lấy nhóm nhưng chỉ dành cho admin
    public List<TransGroupResponse> getGroups(TransGroupStatus status) {

        // Nếu cho query string status thì lấy nhóm dịch theo status
        if (status != null) {
            return transGroupRepository
                    .findAllByStatus(status)
                    .stream()
                    .map(transGroupMapper::toTransGroupResponse)
                    .toList();
        }

        // Nếu ko status thì mặc định lấy tất cả các nhóm
        return transGroupRepository
                .findAll()
                .stream()
                .map(transGroupMapper::toTransGroupResponse)
                .toList();
    }

    @Transactional // cho thêm vì có 1 câu query mình tự định nghĩa, để spring cho vào 1 transaction
    @PreAuthorize("@groupSec.isGroupLeaderOrAdmin(#id)")
    public void softDeleteGroupById(@NonNull String id) {

        TransGroup transGroup = transGroupRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Cập nhập trạng thái nhóm đã bị xoá
        transGroup.setStatus(TransGroupStatus.DELETED);

        // Xoá tất cả các thành viên ra khỏi nhóm (method tự định nghĩa query)
        userRepository.clearTransGroupFromMembers(id);

        transGroupRepository.save(transGroup);
    }


}
