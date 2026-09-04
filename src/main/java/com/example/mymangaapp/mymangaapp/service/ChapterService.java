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
import com.example.mymangaapp.mymangaapp.security.component.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
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

    // Chỉ cần có role translator là tạo được chương
    @PreAuthorize("hasRole('TRANSLATOR') or hasRole('ADMIN')")
    @Transactional
    public ChapterResponse createChapter(@NonNull String mangaId, @NonNull ChapterRequest request) {

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

        // trong 1 bộ manga ko thể có 2 chapter cùng index
        if (chapterRepository.existsByMangaIdAndChapterIndex(mangaId, request.getChapterIndex())) {
            throw new AppException(ResponseCode.CHAPTER_INDEX_ALREADY_EXISTS);
        }

        Chapter chapter = chapterMapper.toChapter(request);
        chapter.setManga(manga);

        // save chapter trước để lấy id cho key của r2
        // khi save vì kiểu của id là UUID nên id được sinh ra trên RAM
        // mà rồi set chuỗi đó vào chapter, nên ko có lệnh insert nào cả
        chapter = chapterRepository.save(chapter);

        // đây là mảng lưu các url của page chính thức
        List<String> pageUrls = new ArrayList<>();

        String prefix = "mangas/" + manga.getId() + "/" + chapter.getId() + "/";

        // Mảng các tmp url của từng ảnh đây
        List<String> tmpPageUrls = request.getPageUrls();

        // Cơ chế bù trừ (Bài toán Bù trừ - Compensating Transaction)
        // Khi copy file, tuy db ko insert rác nhưng trên r2 file đã thay đổi
        // vậy khi hỏng trong qtrình copy file ta sẽ viết cơ chế dọn dẹp ngay tại đó bằng try-catch
        boolean isSuccess = false; // đặt cờ thành công
        try {
            for (int i = 0; i < tmpPageUrls.size(); i++) {

                String tmpUrl = tmpPageUrls.get(i);

                // chỉ lấy phần path của url để làm key
                String tmpKey = new URI(tmpUrl).getPath().substring(1);

                // lấy đuôi file
                String extension = StringUtils.getFilenameExtension(tmpUrl);

                // đổi tên file thành dạng "001", "002"
                String baseName = String.format("%03d", i + 1);

                // Tạo url trên r2 với key là dùng id của manga và chapter
                String objectKey = prefix + baseName + "." + extension;

                // Copy file chính thức nhưng ko xoá file ở tmp vội
                pageUrls.add(storageService.copyFile(tmpKey, objectKey, false));
            }

            isSuccess = true; // copy thành công
            // copy files thành công thì xoá files trong tmp, nếu lỗi uri thì bỏ qua
            tmpPageUrls.parallelStream().forEach(tmpUrl -> {
                try {
                    storageService.deleteFile(new URI(tmpUrl).getPath().substring(1));
                } catch (URISyntaxException ignored) {} // bỏ qua khi lỗi url, ta có cron job lên lịch r ko sao
            });
        } catch (URISyntaxException e) {
            throw new AppException(ResponseCode.URL_INVALID);
        } catch (Exception e) { // Cover hết các exception trong qtrình copy files
            throw new AppException(ResponseCode.FILE_COPY_FAILED);
        } finally {
            if (!isSuccess) {
                log.error("Lỗi trong quá trình copy files!");
            }
        }

        List<Page> pages = pageService.createPages(chapter, pageUrls);
        chapter.setPages(pages);

        return chapterMapper.toChapterResponse(
            chapterRepository.save(chapter)
        );
    }

    @PreAuthorize("hasRole('TRANSLATOR') or hasRole('ADMIN')")
    @Transactional
    public void deleteChapterById(@NonNull String mangaId, @NonNull String chapterId) {

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

        // xoá chapter có cascade và orphanremoval nên page thuộc
        // về chapter cũng sẽ tự động xoá
        chapterRepository.deleteById(chapterId);

        String prefix = "mangas/" + mangaId + "/" + chapterId + "/";

        // gọi xoá tất cả files trong prefix
        storageService.deleteFilesWithPrefix(prefix, Instant.now());

    }

}
