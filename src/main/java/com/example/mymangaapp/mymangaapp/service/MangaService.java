package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.request.MangaCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.MangaMapper;
import com.example.mymangaapp.mymangaapp.repository.MangaRepository;
import com.example.mymangaapp.mymangaapp.repository.TransGroupRepository;
import com.example.mymangaapp.mymangaapp.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    @PreAuthorize("hasAuthority('CREATE_MANGA')")
    public MangaResponse createManga(@NotNull MangaCreationRequest request, @NonNull String id) {

        // Vẫn phải check nhóm tồn tại không
        TransGroup transGroup = transGroupRepository
                .findWithDetailsById(id)
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

        manga.setTransGroups(Set.of(transGroup));

        return mangaMapper.toMangaResponse(mangaRepository.save(manga));
    }

}
