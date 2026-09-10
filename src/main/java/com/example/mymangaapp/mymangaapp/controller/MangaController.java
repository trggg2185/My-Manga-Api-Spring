package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.MangaRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.dto.response.MangaSummaryResponse;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.enums.MangaStatus;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.MangaService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

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

    // public
    // đây là endpoint khi user vào trang của nhóm dịch
    // thì lấy tất cả các bộ truyện mà nhóm đang tham gia dịch
    // cả dịch chính lẫn phụ
    @GetMapping("/transgroups/{groupId}/mangas")
    public ApiResponse<PaginatedResponse<MangaSummaryResponse>> getMangasByGroupId(
            @PathVariable @NonNull String groupId,
            @RequestParam(defaultValue = "0") int page, // số trang
            @RequestParam(defaultValue = "15") int size, // số bản ghi mỗi trang
            @RequestParam(defaultValue = "createdAt") String sortBy // sxep theo field nào
    ) {
        PaginatedResponse<MangaSummaryResponse> responses = mangaService.getMangasByGroupId(groupId, page, size, sortBy);

        return ApiResponse.<PaginatedResponse<MangaSummaryResponse>>builder()
                .result(responses)
                .build();
    }

    @DeleteMapping("/transgroups/{groupId}/mangas/{mangaId}")
    public ApiResponse<String> deleteMangaById(
            @PathVariable @NonNull String groupId,
            @PathVariable @NonNull String mangaId
    ) {

        mangaService.deleteMangaById(groupId, mangaId);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("Transgroup id: " + groupId + ", manga id: " + mangaId)
                .build();
    }

    // lấy tất manga public có pagination
    // đây là khi họ vào trang home của web
    // sẽ lấy tất cả các manga có phân trang, những chỉ lấy với
    // chút thông tin của manga như: name, categories, description, transgroups
    @GetMapping("/mangas")
    public ApiResponse<PaginatedResponse<MangaSummaryResponse>> getMangas(
            @RequestParam(defaultValue = "0") int page, // số trang
            @RequestParam(defaultValue = "24") int size, // số bản ghi mỗi trang
            @RequestParam(defaultValue = "createdAt") String sortBy // sxep theo field nào
    ) {
        PaginatedResponse<MangaSummaryResponse> responses = mangaService.getMangas(page, size, sortBy);

        return ApiResponse.<PaginatedResponse<MangaSummaryResponse>>builder()
                .result(responses)
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
    public ApiResponse<PaginatedResponse<MangaResponse>> getMangas(
            @RequestParam(required = false) MangaStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
            ) {
        PaginatedResponse<MangaResponse> responses = mangaService.getMangas(status, page, size, sortBy);

        return ApiResponse.<PaginatedResponse<MangaResponse>>builder()
                .result(responses)
                .build();
    }

}
