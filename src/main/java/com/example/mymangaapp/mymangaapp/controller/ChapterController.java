package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.ChapterRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.ChapterResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.ChapterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterController {

    ChapterService chapterService;

    @PostMapping("/mangas/{mangaId}/chapters")
    public ApiResponse<ChapterResponse> createChapter(
            @PathVariable @NonNull String mangaId,
            @RequestBody ChapterRequest request
    ) {

        ChapterResponse response = chapterService.createChapter(mangaId, request);

        return ApiResponse.<ChapterResponse>builder()
                .result(response)
                .build();
    }

    // Mặc dù tên hàm là xoá theo id chapter nhưng ko phải nhóm nào cũng xoá đc
    // chỉ có chapter thuộc về manga của nhóm đó mới xoá đc nhé
    @DeleteMapping("/mangas/{mangaId}/chapters/{chapterId}")
    public ApiResponse<String> deleteChapterById(
            @PathVariable @NonNull String mangaId,
            @PathVariable @NonNull String chapterId
    ) {

        chapterService.deleteChapterById(mangaId, chapterId);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("Chapter id: " + chapterId + ", manga id: " + mangaId)
                .build();

    }

}
