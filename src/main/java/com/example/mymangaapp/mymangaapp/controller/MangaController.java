package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.MangaRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.enums.MangaStatus;
import com.example.mymangaapp.mymangaapp.service.MangaService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MangaController {

    MangaService mangaService;

    @PostMapping("/transgroups/{groupId}/mangas")
    public ApiResponse<MangaResponse> createManga(
            @Valid @RequestBody MangaRequest request,
            @PathVariable @NonNull String groupId
    ) {
        MangaResponse response = mangaService.createManga(request, groupId);

        return ApiResponse.<MangaResponse>builder()
                .result(response)
                .build();
    }

    @PatchMapping("/transgroups/{groupId}/mangas/{mangaId}")
    public ApiResponse<MangaResponse> updateMangaById(
            @PathVariable @NonNull String groupId,
            @PathVariable @NonNull String mangaId,
            @RequestBody MangaRequest request
    ) {
        MangaResponse response = mangaService.updateMangaById(groupId, mangaId, request);

        return ApiResponse.<MangaResponse>builder()
                .result(response)
                .build();
    }



    @GetMapping("/mangas/{id}")
    public ApiResponse<MangaResponse> getMangaById(
            @PathVariable @NonNull String id
    ) {
        MangaResponse response = mangaService.getMangaById(id);

        return ApiResponse.<MangaResponse>builder()
                .result(response)
                .build();
    }

    // admin only
    @GetMapping("/admin/mangas")
    public ApiResponse<List<MangaResponse>> getMangas(
            @RequestParam(required = false) MangaStatus status
            ) {
        List<MangaResponse> responses = mangaService.getMangas(status);

        return ApiResponse.<List<MangaResponse>>builder()
                .result(responses)
                .build();
    }

}
