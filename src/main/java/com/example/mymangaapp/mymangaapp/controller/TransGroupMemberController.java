package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.common.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.transgroup.response.DeleteMemberResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.TransGroupMemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransGroupMemberController {

    TransGroupMemberService transGroupMemberService;

    @DeleteMapping("/transgroups/{groupId}/users/{userId}")
    public ApiResponse<DeleteMemberResponse> kickMember(
            @PathVariable String groupId,
            @PathVariable String userId
    ) {

        transGroupMemberService.kickMember(groupId, userId);

        return ApiResponse.<DeleteMemberResponse>builder()
                .message(ResponseCode.SUCCESS.getMessage())
                .result(DeleteMemberResponse.builder().groupId(groupId).memberId(userId).build())
                .build();
    }

}
