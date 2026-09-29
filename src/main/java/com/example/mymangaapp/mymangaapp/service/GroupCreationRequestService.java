package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.request.CreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.CreationRequestResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.dto.transgroup.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.entity.GroupCreationRequest;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.enums.GroupCreationRequestStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.GroupCreationRequestMapper;
import com.example.mymangaapp.mymangaapp.mapper.TransGroupMapper;
import com.example.mymangaapp.mymangaapp.repository.GroupCreationRequestRepository;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;
import com.example.mymangaapp.mymangaapp.security.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GroupCreationRequestService {

    GroupCreationRequestRepository groupCreationRequestRepository;
    UserRepository userRepository;

    GroupCreationRequestMapper groupCreationRequestMapper;
    TransGroupMapper transGroupMapper;

    TransGroupService transGroupService;


    // -----------------------------chức năng dành cho user đã đăng nhập -------------------------------------

    // Yêu cầu tạo nhóm dịch
    @Transactional
    public CreationRequestResponse requestCreateGroup(@NonNull CreationRequest request) {

        String currentUserId = SecurityUtils.getCurrentUserId();

        // check yc tồn tại
        if (groupCreationRequestRepository.existsByCreatorIdAndStatus(currentUserId, GroupCreationRequestStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_CREATION_REQUEST_ALREADY_EXISTED);
        }

        User user = userRepository
                .findById(currentUserId)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        // check user là thành viên của nhóm dịch nào chưa
        if (user.getTransGroup() != null) {
            throw new AppException(ResponseCode.USER_ALREADY_IN_GROUP);
        }

        GroupCreationRequest groupCreationRequest = groupCreationRequestMapper.toGroupCreationRequest(request);
        groupCreationRequest.setCreator(user);

        return groupCreationRequestMapper.toCreationRequestResponse(
                groupCreationRequestRepository.save(groupCreationRequest)
        );
    }


    // ----------------------------------chức năng cho admin ----------------------------------------

    // duyệt việc tạo nhóm
    @Transactional
    public TransGroupResponse approveCreateGroup(@NonNull String requestId) {

        GroupCreationRequest groupCreationRequest = groupCreationRequestRepository
                .findWithCreatorById(requestId)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_CREATION_REQUEST_NOT_FOUND));

        // check phải dg ở trg thái pending thì mới duyệt đc
        if (!groupCreationRequest.getStatus().equals(GroupCreationRequestStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_CREATION_REQUEST_STATUS_INVALID);
        }

        User reviewer = userRepository
                .findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        // duyệt nhóm từ admin
        groupCreationRequest.setStatus(GroupCreationRequestStatus.APPROVED);

        // tạo và lưu nhóm trước đã
        TransGroup createdGroup = transGroupService.createTransGroup(groupCreationRequest);

        // rồi set lại yêu cầu này giúp nhóm nào được tạo
        groupCreationRequest.setCreatedGroup(createdGroup);
        // set luôn admin nào duyệt
        groupCreationRequest.setReviewer(reviewer);

        // lưu lại
        groupCreationRequestRepository.save(groupCreationRequest);

        return transGroupMapper.toTransGroupResponse(createdGroup);
    }

    // từ chối việc tạo nhóm
    @Transactional
    public CreationRequestResponse rejectCreateGroup(@NonNull String requestId) {

        GroupCreationRequest groupCreationRequest = groupCreationRequestRepository
                .findWithCreatorById(requestId)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_CREATION_REQUEST_NOT_FOUND));

        // check phải dg ở trg thái pending thì từ chối đc
        if (!groupCreationRequest.getStatus().equals(GroupCreationRequestStatus.PENDING)) {
            throw new AppException(ResponseCode.TRANSGROUP_CREATION_REQUEST_STATUS_INVALID);
        }

        User reviewer = userRepository
                .findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        groupCreationRequest.setStatus(GroupCreationRequestStatus.REJECTD);
        groupCreationRequest.setReviewer(reviewer);

        return groupCreationRequestMapper.toCreationRequestResponse(
                groupCreationRequestRepository.save(groupCreationRequest)
        );
    }

    // Lấy tất cả những yêu cầu tạo group
    public PaginatedResponse<CreationRequestResponse> getAllCreationRequests(
            GroupCreationRequestStatus status, int page,
            int size, @NonNull String sortBy
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<CreationRequestResponse> dtoPage;

        if (status != null) {
            dtoPage = groupCreationRequestRepository
                    .findAllByStatus(status, pageable)
                    .map(groupCreationRequestMapper::toCreationRequestResponse);
        } else {
            dtoPage = groupCreationRequestRepository
                    .findAll(pageable)
                    .map(groupCreationRequestMapper::toCreationRequestResponse);
        }

        return PaginatedResponse.of(dtoPage);
    }
}
