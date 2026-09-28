package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.transgroup.TransGroupUpdateRequest;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.dto.transgroup.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.entity.GroupCreationRequest;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.TransGroupMapper;
import com.example.mymangaapp.mymangaapp.repository.TransGroupRepository;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransGroupService {

    UserRepository userRepository;
    TransGroupRepository transGroupRepository;

    TransGroupMapper transGroupMapper;

    // method này đc gọi từ method đã có transactional thì method này ko cần transactional
    // vì method này khác tự tham gia vào transaction đó, khi fail thì rollback toàn bộ 2 method
    // hàm giúp tạo nhóm từ yêu cầu tạo nhóm
    @Transactional // vẫn nên thêm transactional để khi có method nơi khác gọi
    public TransGroup createTransGroup(GroupCreationRequest groupCreationRequest) {

        // quyết định là trong members sẽ ko chứa leader
        TransGroup transGroup = TransGroup.builder()
                .name(groupCreationRequest.getNameGroup())
                .description(groupCreationRequest.getDescription())
                .leader(groupCreationRequest.getCreator())
                .build();

        return transGroupRepository.save(transGroup);
    }


    // ----------------------------------- chức năng public (cho khách) -----------------------------------//

    // Lấy tất cả nhóm dịch đã được chấp thuận, public
    public PaginatedResponse<TransGroupResponse> getGroups(
            int page, int size,
            @NonNull String sortBy
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<TransGroupResponse> dtoPage = transGroupRepository
                .findAllByStatus(TransGroupStatus.APPROVED, pageable)
                .map(transGroupMapper::toTransGroupResponse);

        return PaginatedResponse.of(dtoPage);
    }


    // -------------------------------- chức năng của admin -------------------------------- //

    // Đây cũng lấy nhóm nhưng chỉ dành cho admin
    public PaginatedResponse<TransGroupResponse> getGroups(
            TransGroupStatus status, int page,
            int size, @NonNull String sortBy
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<TransGroupResponse> dtoPage;

        // Nếu cho query string status thì lấy nhóm dịch theo status
        if (status != null) {
            dtoPage = transGroupRepository
                    .findAllByStatus(status, pageable)
                    .map(transGroupMapper::toTransGroupResponse);
        } else { // Nếu ko status thì mặc định lấy tất cả các nhóm
            dtoPage = transGroupRepository
                    .findAll(pageable)
                    .map(transGroupMapper::toTransGroupResponse);
        }

        return PaginatedResponse.of(dtoPage);
    }


    // ------------------------ chức năng dành cho leader nhóm dịch hoặc admin ------------------------ //

    // xoá nhóm dịch, admin cho đc tất, leader chỉ xoá đc nhóm của mình
    @Transactional // cho thêm vì có 1 câu query mình tự định nghĩa, để spring cho vào 1 transaction
    @PreAuthorize("@groupSec.isGroupLeaderOrAdmin(#id)")
    public void softDeleteGroupById(@NonNull String id) {

        TransGroup transGroup = transGroupRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Cập nhập trạng thái nhóm đã bị xoá
        transGroup.setStatus(TransGroupStatus.DELETED);

        // Xoá tất role TRANSLATOR ra tất cả các thành viên
        // Method này phải chạy trc method clear, nếu chạy sau
        // thì members trong group bị clear hết thì method này ko hoạt động nữa
        userRepository.removeTranslatorRoleFromMembers(id);

        // Xoá tất cả các thành viên ra khỏi nhóm (method tự định nghĩa query)
        userRepository.clearTransGroupFromMembers(id);

        transGroupRepository.save(transGroup);
    }

    // cập nhật thông tin group (name và description)
    @PreAuthorize("@groupSec.isGroupLeaderOrAdmin(#id)")
    public TransGroupResponse updateGroupById(@NonNull String id, @NonNull TransGroupUpdateRequest request) {

        TransGroup transGroup = transGroupRepository
                .findWithDetailsById(id)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        transGroupMapper.updateTransGroupFromRequest(transGroup, request);

        return transGroupMapper.toTransGroupResponse(transGroupRepository.save(transGroup));

    }


}
