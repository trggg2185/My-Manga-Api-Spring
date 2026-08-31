package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.request.ChapterRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ChapterResponse;
import com.example.mymangaapp.mymangaapp.entity.Chapter;
import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.entity.Page;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.ChapterMapper;
import com.example.mymangaapp.mymangaapp.repository.ChapterRepository;
import com.example.mymangaapp.mymangaapp.repository.MangaRepository;
import com.example.mymangaapp.mymangaapp.utils.SecurityUtils;
import com.github.slugify.Slugify;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChapterService {

    ChapterRepository chapterRepository;
    MangaRepository mangaRepository;

    ChapterMapper chapterMapper;

    StorageService storageService;
    PageService pageService;

    Slugify slugify;

    // Chỉ cần có role translator là tạo được chương
    @PreAuthorize("hasRole('TRANSLATOR')")
    @Transactional
    public ChapterResponse createChapter(@NonNull String mangaId, @NotNull ChapterRequest request) {

        Manga manga = mangaRepository
                .findById(mangaId)
                .orElseThrow(() -> new AppException(ResponseCode.MANGA_NOT_FOUND));

        String currentUsername = SecurityUtils.getCurrentUsername();

        // Chỉ cần user hiện tại là thành viên trong những
        // nhóm đang dịch manga này là được
        boolean isMember = manga.getTransGroups().stream()
                .flatMap(transGroup -> transGroup.getMembers().stream())
                .anyMatch(member -> member.getUsername().equals(currentUsername));
        if (!isMember) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        Chapter chapter = chapterMapper.toChapter(request);

        // đây là mảng lưu các url của page chính thức
        List<String> pageUrls = new ArrayList<>();

        String slugifiedMangaName = slugify.slugify(manga.getName());
        String slugifiedChapterName = slugify.slugify(chapter.getName());

        // Mảng các tmp url của từng ảnh đây
        List<String> tmpPageUrls = request.getPageUrls();
        for (int i = 0; i < tmpPageUrls.size(); i++) {

            String tmpUrl = tmpPageUrls.get(i);
            // chỉ lấy phần path của url để làm key
            String tmpKey = tmpUrl.substring(tmpUrl.indexOf("tmp"));

            // lấy đuôi file
            String extension = StringUtils.getFilenameExtension(tmpUrl);

            // đổi tên file thành dạng "001", "002"
            String baseName = String.format("%03d", i + 1);

            // Tạo url trên r2
            String objectKey = "mangas/" + slugifiedMangaName + "/"
                    + slugifiedChapterName + "/"
                    + baseName + "." + extension;
            log.info("Temp key: {}", tmpKey);
            log.info("Object key: {}", objectKey);

            // Copy file chính thức và xoá file tmp
            pageUrls.add(storageService.copyFile(tmpKey, objectKey, true));

        }

        List<Page> pages = pageService.createPages(chapter, pageUrls);
        chapter.setPages(pages);
        chapter.setManga(manga);

        return chapterMapper.toChapterResponse(
            chapterRepository.save(chapter)
        );
    }

}
