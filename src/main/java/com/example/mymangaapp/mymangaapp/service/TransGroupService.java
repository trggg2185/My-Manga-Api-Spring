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
    @PreAuthorize("hasRole('USER')") // tức là phải đăng nhập mới có quyền
    public TransGroupResponse requestCreateGroup(@NotNull TransGroupCreationRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        if (transGroupRepository.existsByName(request.getName())) {
            throw new AppException(ResponseCode.TRANSGROUP_NAME_ALREADY_EXISTS);
        }

        User leader = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        TransGroup transGroup = transGroupMapper.toTransGroup(request);
        transGroup.setLeader(leader);
        transGroup.setMembers(new HashSet<>());

        // Trưởng nhóm dịch cũng là 1 thành viên
        transGroup.getMembers().add(leader);

        transGroup = transGroupRepository.save(transGroup);

        // Cho user thuộc về nhóm dịch
        leader.setTransGroup(transGroup);
        userRepository.save(leader);

        return transGroupMapper.toTransGroupResponse(transGroup);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public TransGroupResponse approveCreateGroup(@NonNull String id) {

        TransGroup transGroup = transGroupRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Duyệt nhóm tức là nhóm phải đang ở trạng thái chờ (PENDING) -> chấp nhận (APPROVED)
        if (!transGroup.getStatus().equals(TransGroupStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_STATUS_INVALID);
        }

        transGroup.setStatus(TransGroupStatus.APPROVED);

        return transGroupMapper.toTransGroupResponse(transGroupRepository.save(transGroup));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public TransGroupResponse rejectCreateGroup(@NonNull String id) {

        TransGroup transGroup = transGroupRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Từ chối nhóm tức là nhóm phải đang ở trạng thái chờ (PENDING) -> từ chối (REJECTED)
        if (!transGroup.getStatus().equals(TransGroupStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_STATUS_INVALID);
        }

        transGroup.setStatus(TransGroupStatus.REJECTED);

        return transGroupMapper.toTransGroupResponse(transGroupRepository.save(transGroup));
    }

    public List<TransGroupResponse> getAllGroups() {
        List<TransGroup> transGroups = transGroupRepository.findAll();

        return transGroups
                .stream()
                // <=> transGroup -> transGroupMapper.toTransGroupResponse(transGroup)
                .map(transGroupMapper::toTransGroupResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TransGroupResponse> getPendingGroups() {

        List<TransGroup> pendingTransGroup = transGroupRepository.findAllByStatus(TransGroupStatus.PENDING);

        return pendingTransGroup
                .stream()
                .map(transGroupMapper::toTransGroupResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TransGroupResponse> getApprovedGroups() {

        List<TransGroup> approvedTransGroups = transGroupRepository.findAllByStatus(TransGroupStatus.APPROVED);

        return approvedTransGroups
                .stream()
                .map(transGroupMapper::toTransGroupResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TransGroupResponse> getRejectdGroups() {
        List<TransGroup> rejectedTransGroups = transGroupRepository.findAllByStatus(TransGroupStatus.REJECTED);

        return rejectedTransGroups
                .stream()
                // <=> transGroup -> transGroupMapper.toTransGroupResponse(transGroup)
                .map(transGroupMapper::toTransGroupResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TransGroupResponse> getDeletedGroups() {
        List<TransGroup> deletedTransGroups = transGroupRepository.findAllByStatus(TransGroupStatus.DELETED);

        return deletedTransGroups
                .stream()
                // <=> transGroup -> transGroupMapper.toTransGroupResponse(transGroup)
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
