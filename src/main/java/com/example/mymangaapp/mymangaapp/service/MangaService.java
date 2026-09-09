package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.request.MangaRequest;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.dto.response.MangaSummaryResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.enums.MangaStatus;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.MangaMapper;
import com.example.mymangaapp.mymangaapp.repository.MangaRepository;
import com.example.mymangaapp.mymangaapp.repository.TransGroupRepository;
import jakarta.transaction.Transactional;
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

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MangaService {

    MangaRepository mangaRepository;
    TransGroupRepository transGroupRepository;

    MangaMapper mangaMapper;

    // cần role translator và user hiện tại phải là leader nhóm mới có quyền
    @PreAuthorize("hasAnyRole('ADMIN', 'TRANSLATOR') and @groupSec.isGroupLeader(#groupId)")
    @Transactional
    public MangaResponse createManga(@NonNull MangaRequest request, @NonNull String groupId) {

        // Vẫn phải check nhóm tồn tại không
        TransGroup ownerTransGroup = transGroupRepository
                .findWithDetailsById(groupId)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Nhóm này đã được admin approve chưa
        if (!ownerTransGroup.getStatus().equals(TransGroupStatus.APPROVED)) {
            throw new AppException(ResponseCode.TRANSGROUP_NOT_APPROVED);
        }

        Manga manga = mangaMapper.toManga(request);
        manga.setOwnerTransGroup(ownerTransGroup);
        manga.setTransGroups(Set.of(ownerTransGroup));

        return mangaMapper.toMangaResponse(mangaRepository.save(manga));
    }

    // chỉ dành cho admin
    public PaginatedResponse<MangaResponse> getMangas(
            MangaStatus status, @NonNull int page,
            @NonNull int size, @NonNull String sortBy
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<MangaResponse> dtoPage;

        if (status != null) {
            dtoPage = mangaRepository
                    .findAllByStatus(status, pageable)
                    .map(mangaMapper::toMangaResponse);
        } else {
            dtoPage = mangaRepository
                    .findAll(pageable)
                    .map(mangaMapper::toMangaResponse);
        }

        return PaginatedResponse.of(dtoPage);
    }

    // lấy tất manga, public có pagination
    public PaginatedResponse<MangaResponse> getMangas(int page, int size, String sortBy) {

        log.info("page: {}, size: {}, sort by: {}", page, size, sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<MangaResponse> dtoPage = mangaRepository
                .findAll(pageable)
                .map(mangaMapper::toMangaResponse);

        return PaginatedResponse.of(dtoPage);
    }

    // public
    public PaginatedResponse<MangaSummaryResponse> getMangasByGroupId(
            @NonNull String groupId, @NonNull int page,
            @NonNull int size, @NonNull String sortBy
    ) {

        if (!transGroupRepository.existsById(groupId)) {
            throw new AppException(ResponseCode.TRANSGROUP_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<MangaSummaryResponse> dtoPage = mangaRepository
                .findAllByTransGroupsId(groupId, pageable)
                .map(mangaMapper::toMangaSummaryResponse);

        return PaginatedResponse.of(dtoPage);
    }

    // public
    public MangaResponse getMangaById(@NonNull String id) {
        Manga manga = mangaRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ResponseCode.MANGA_NOT_FOUND));

        return mangaMapper.toMangaResponse(manga);
    }

    // chỉ dành cho leader
    @PreAuthorize("@groupSec.isGroupLeader(#groupId)")
    @Transactional
    public MangaResponse updateMangaById(@NonNull String groupId, @NonNull String mangaId, @NonNull MangaRequest request) {

        Manga manga = mangaRepository
                .findById(mangaId)
                .orElseThrow(() -> new AppException(ResponseCode.MANGA_NOT_FOUND));

        // Những nhóm khác cùng dịch 1 bộ thì ko có quyền sửa thông tin manga đâu
        // chỉ có nhóm chủ sở hữu bộ này thôi

        // Check xem manga này đúng sở hữu bởi nhóm không
        // nhỡ manga của nhóm khác mà update thì toi
        if (!manga.getOwnerTransGroup().getId().equals(groupId)) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        mangaMapper.updateMangaFromRequest(manga, request);

        return mangaMapper.toMangaResponse(mangaRepository.save(manga));
    }

    // làm sau
    public void deleteMangaById(@NonNull String groupId, @NonNull String mangaId) {

    }

}
