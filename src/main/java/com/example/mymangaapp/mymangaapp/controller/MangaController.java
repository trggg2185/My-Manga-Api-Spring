package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.MangaCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.service.MangaService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mangas")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MangaController {

    MangaService mangaService;

    @PostMapping("/transgroups/{groupId}/mangas")
    public ApiResponse<MangaResponse> createManga(
            @Valid @RequestBody MangaCreationRequest request,
            @PathVariable String groupId
    ) {
        MangaResponse response = mangaService.createManga(request, groupId);

        return ApiResponse.<MangaResponse>builder()
                .result(response)
                .build();
    }

}
