package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.request.TransGroupCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.dto.response.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.service.TransGroupService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transgroups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransGroupController {

    TransGroupService transGroupService;

    @PostMapping
    public ApiResponse<TransGroupResponse> requestCreateGroup(@RequestBody TransGroupCreationRequest request) {
        TransGroupResponse response = transGroupService.requestCreateGroup(request);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<TransGroupResponse>> getAllTransGroups() {
        List<TransGroupResponse> responses = transGroupService.getAllTransGroups();

        return ApiResponse.<List<TransGroupResponse>>builder()
                .result(responses)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> softDeleteGroupById(@PathVariable @NonNull String id) {
        transGroupService.softDeleteGroupById(id);

        return ApiResponse.<String>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .result("Group id: " + id)
                .build();
    }

    @PatchMapping("/{id}/approve")
    public ApiResponse<TransGroupResponse> approveCreateGroup(@PathVariable @NonNull String id) {
        TransGroupResponse response = transGroupService.approveCreateGroup(id);

        return ApiResponse.<TransGroupResponse>builder()
                .result(response)
                .build();
    }

}
