package com.example.mymangaapp.mymangaapp.service;

import com.example.mymangaapp.mymangaapp.dto.request.ChapterRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ChapterResponse;
import com.example.mymangaapp.mymangaapp.dto.response.ChapterSummaryResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.entity.Chapter;
import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.entity.Page;
import com.example.mymangaapp.mymangaapp.enums.TransGroupStatus;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.ChapterMapper;
import com.example.mymangaapp.mymangaapp.repository.ChapterRepository;
import com.example.mymangaapp.mymangaapp.repository.MangaRepository;
import com.example.mymangaapp.mymangaapp.security.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

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


    // ------------------chức năng dành cho translator hoặc admin nhưng vẫn phải là thành viên của nhóm mới được------------------------//

    @PreAuthorize("hasAnyRole('ADMIN', 'TRANSLATOR')")
    @Transactional
    public ChapterResponse createChapter(@NonNull String mangaId, @NonNull ChapterRequest request) {

        Manga manga = mangaRepository
                .findById(mangaId)
                .orElseThrow(() -> new AppException(ResponseCode.MANGA_NOT_FOUND));

        String currentUserId = SecurityUtils.getCurrentUserId();

        // user phải là thành viên hoặc leader của các nhóm đang dịch truyện này
        // và nhóm dịch phải hoạt động thì mới được tạo chương
        if (!mangaRepository.isMemberOrLeaderOfAnyGroupOfManga(currentUserId, mangaId, TransGroupStatus.APPROVED)) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        // trong 1 bộ manga ko thể có 2 chapter cùng index
        if (chapterRepository.existsByMangaIdAndChapterIndex(mangaId, request.getChapterIndex())) {
            throw new AppException(ResponseCode.CHAPTER_INDEX_ALREADY_EXISTED);
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
                String tmpKey = storageService.parseObbjectKeyFromUrl(tmpUrl);

                // tránh user đoán đc key của các truyện khác trong folder mangas/
                // rồi sửa page url, phải check xem là up từ tmp/ lên
                if (!tmpKey.startsWith("tmp/")) {
                    throw new AppException(ResponseCode.UNAUTHORIZED);
                }

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
        } catch (URISyntaxException e) {
            throw new AppException(ResponseCode.URL_INVALID);
        } catch (SdkClientException e) { // lỗi phía client
            throw new AppException(ResponseCode.STORAGE_SERVICE_UNAVAILABLE);
        } catch (S3Exception e) { // lỗi phía dịch vụ
            throw new AppException(ResponseCode.STORAGE_SERVICE_ERROR);
        } finally {
            if (!isSuccess) {
                log.error("Lỗi trong quá trình copy files!");
                // Lỗi thì xoá luôn những files đã copy thành công trc đó
                storageService.deleteFilesWithPrefix(prefix, Instant.now());
            }
        }

        List<Page> pages = pageService.createPages(chapter, pageUrls);
        chapter.setPages(pages);

        return chapterMapper.toChapterResponse(
            chapter
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TRANSLATOR')")
    @Transactional
    public void deleteChapterById(@NonNull String mangaId, @NonNull String chapterId) {

        Chapter chapter = chapterRepository
                .findById(chapterId)
                .orElseThrow(() -> new AppException(ResponseCode.CHAPTER_NOT_FOUND));

        // Check chapter có thuộc về manga này ko
        if (!chapter.getManga().getId().equals(mangaId)) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        String currentUserId = SecurityUtils.getCurrentUserId();

        // user phải là thành viên hoặc leader của các nhóm đang dịch truyện này
        // và nhóm dịch phải hoạt động thì mới được tạo chương
        if (!mangaRepository.isMemberOrLeaderOfAnyGroupOfManga(currentUserId, mangaId, TransGroupStatus.APPROVED)) {
            throw new AppException(ResponseCode.UNAUTHORIZED);
        }

        // xoá chapter có cascade và orphanremoval nên page thuộc
        // về chapter cũng sẽ tự động xoá
        chapterRepository.delete(chapter);

        String prefix = "mangas/" + mangaId + "/" + chapterId + "/";

        // gọi xoá tất cả files trong prefix
        storageService.deleteFilesWithPrefix(prefix, Instant.now());

    }


    // ------------------------------chức năng public (cho khách)---------------------------------//

    // user thì ai cx vào manga bất kỳ và đều đọc đc các chapters
    // ==> để public method này
    public PaginatedResponse<ChapterSummaryResponse> getAllChaptersByMangaId(
            @NonNull String mangaId, int page,
            int size, @NonNull String sortBy
    ) {

        if (!mangaRepository.existsById(mangaId)) {
            throw new AppException(ResponseCode.MANGA_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        org.springframework.data.domain.Page<ChapterSummaryResponse> dtoPage = chapterRepository
                .findByMangaId(mangaId, pageable)
                .map(chapterMapper::toChapterSummaryResponse);

        return PaginatedResponse.of(dtoPage);
    }

}
