package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.JoinRequestResponse;
import com.example.mymangaapp.mymangaapp.enums.GroupJoinRequestStatus;
import com.example.mymangaapp.mymangaapp.service.GroupJoinRequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupJoinRequestController {

    GroupJoinRequestService groupJoinRequestService;

    // 1 user xin vào làm thành viên của 1 nhóm dịch
    @PostMapping("/transgroups/{groupId}/join-requests")
    public ApiResponse<JoinRequestResponse> requestJoinGroup(@PathVariable @NonNull String groupId) {
        JoinRequestResponse response = groupJoinRequestService.requestJoinGroup(groupId);

        return ApiResponse.<JoinRequestResponse>builder()
                .result(response)
                .build();
    }

    // Lấy danh sách các yêu cầu xin vào nhóm (leader only)
    @GetMapping("/transgroups/{groupId}/join-requests")
    public ApiResponse<List<JoinRequestResponse>> getJoinRequests(
            @RequestParam(required = false) GroupJoinRequestStatus status,
            @PathVariable @NonNull String groupId
            ) {
        List<JoinRequestResponse> responses = groupJoinRequestService.getJoinRequests(status, groupId);

        return ApiResponse.<List<JoinRequestResponse>>builder()
                .result(responses)
                .build();
    }

    @PatchMapping("/transgroups/{groupId}/join-requests/{requestId}/approve")
    public ApiResponse<JoinRequestResponse> approveJoinGroup(
            @PathVariable @NonNull String groupId,
            @PathVariable @NonNull String requestId
    ) {

        JoinRequestResponse response = groupJoinRequestService.approveJoinGroup(groupId, requestId);

        return ApiResponse.<JoinRequestResponse>builder()
                .result(response)
                .build();
    }

}
