package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.request.MangaRequest;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.enums.MangaStatus;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.MangaMapper;
import com.example.mymangaapp.mymangaapp.repository.MangaRepository;
import com.example.mymangaapp.mymangaapp.repository.TransGroupRepository;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MangaService {

    MangaRepository mangaRepository;
    TransGroupRepository transGroupRepository;

    MangaMapper mangaMapper;

    @PreAuthorize("hasAuthority('CREATE_MANGA')")
    @Transactional
    public MangaResponse createManga(@NotNull MangaRequest request, @NonNull String groupId) {

        // Vẫn phải check nhóm tồn tại không
        TransGroup transGroup = transGroupRepository
                .findWithDetailsById(groupId)
                .orElseThrow(() -> new AppException(ResponseCode.TRANSGROUP_NOT_FOUND));

        // Nhóm này đã được admin approve chưa
        if (!transGroup.getStatus().equals(TransGroupStatus.APPROVED)) {
            throw new AppException(ResponseCode.TRANSGROUP_NOT_APPROVED);
        }

        String currentUsername = SecurityUtils.getCurrentUsername();

        // Check user hiện tại phải là leader của trans group thì mới được tạo
        if (!transGroup.getLeader().getUsername().equals(currentUsername)) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        Manga manga = mangaMapper.toManga(request);

        manga.setTransGroups(List.of(transGroup));

        return mangaMapper.toMangaResponse(mangaRepository.save(manga));
    }

    // chỉ dành cho admin
    public List<MangaResponse> getMangas(MangaStatus status) {

        if (status != null) {
            return mangaRepository
                    .findAllByStatus(status)
                    .stream()
                    .map(mangaMapper::toMangaResponse)
                    .toList();
        }

        return mangaRepository
                .findAll()
                .stream()
                .map(mangaMapper::toMangaResponse)
                .toList();
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
    public MangaResponse updateMangaById(@NonNull String groupId, @NonNull String mangaId, @NotNull MangaRequest request) {

        Manga manga = mangaRepository
                .findById(mangaId)
                .orElseThrow(() -> new AppException(ResponseCode.MANGA_NOT_FOUND));

        // Check xem manga này đúng là của nhóm không
        // nhỡ manga của nhóm khác mà update thì toi
        boolean isOwnManga = manga.getTransGroups()
                .stream()
                .anyMatch(transGroup -> transGroup.getId().equals(groupId));
        if (!isOwnManga) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        mangaMapper.updateMangaFromRequest(manga, request);

        return mangaMapper.toMangaResponse(mangaRepository.save(manga));
    }

}
