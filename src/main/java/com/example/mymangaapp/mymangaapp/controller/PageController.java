package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PageResponse;
import com.example.mymangaapp.mymangaapp.service.PageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PageController {

    PageService pageService;

    @GetMapping("/chapters/{chapterId}/pages")
    public ApiResponse<List<PageResponse>> getChaptersByMangaId(@PathVariable @NonNull String chapterId) {

        List<PageResponse> responses = pageService.getAllPagesByChapterId(chapterId);

        return ApiResponse.<List<PageResponse>>builder()
                .result(responses)
                .build();
    }
}
