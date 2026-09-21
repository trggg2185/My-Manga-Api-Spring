package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.annotation.RateLimit;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PageResponse;
import com.example.mymangaapp.mymangaapp.security.utils.SecurityUtils;
import com.example.mymangaapp.mymangaapp.service.ChapterViewService;
import com.example.mymangaapp.mymangaapp.service.PageService;
import com.example.mymangaapp.mymangaapp.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
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

    ChapterViewService chapterViewService;


    // -------------------------------endpoints public ----------------------------------//

    @GetMapping("/chapters/{chapterId}/pages")
    @RateLimit(capacity = 120)
    public ApiResponse<List<PageResponse>> getAllPagesByChapterId(
            @PathVariable @NonNull String chapterId,
            HttpServletRequest request
    ) {

        List<PageResponse> responses = pageService.getAllPagesByChapterId(chapterId);

        String userId = SecurityUtils.getCurrentUserId();
        String clientId = HttpUtils.getClientIp(request);

        chapterViewService.incrementView(chapterId, userId, clientId);

        return ApiResponse.<List<PageResponse>>builder()
                .result(responses)
                .build();
    }
}
