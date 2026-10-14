package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.common.ApiResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.MangaTransGroupService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MangaTransGroupController {

    MangaTransGroupService mangaTransGroupService;

    @DeleteMapping("/mangas/{mangaId}/transgroups/{groupId}")
    // cả nhóm dịch rời, ko dịch truyện này nữa
    public ApiResponse<Void> leave(
            @PathVariable @NonNull String mangaId,
            @PathVariable @NonNull String groupId
    ) {
        mangaTransGroupService.leave(mangaId, groupId);

        return ApiResponse.<Void>builder()
                .message(ResponseCode.SUCCESS.getMessage())
                .code(ResponseCode.SUCCESS.getCode())
                .build();
    }

}
