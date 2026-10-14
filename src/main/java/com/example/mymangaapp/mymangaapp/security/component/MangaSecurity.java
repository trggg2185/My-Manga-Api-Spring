package com.example.mymangaapp.mymangaapp.security.component;

import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.repository.MangaRepository;
import com.example.mymangaapp.mymangaapp.security.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("mangaSec")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MangaSecurity {

    MangaRepository mangaRepository;

    public boolean isLeaderOfManga(String mangaId) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        return mangaRepository.isLeaderOfManga(currentUserId, mangaId, TransGroupStatus.APPROVED);
    }

}
