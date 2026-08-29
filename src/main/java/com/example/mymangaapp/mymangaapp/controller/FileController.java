package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.service.StorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

    StorageService storageService;

    @PostMapping("/files/upload-tmp")
    public ApiResponse<String> uploadTmpFile(@RequestPart("file") MultipartFile file) {

        String response = storageService.uploadTmpFile(file);

        return ApiResponse.<String>builder()
                .result(response)
                .build();
    }

    @PostMapping("/files/upload-multi-tmp")
    public ApiResponse<List<String>> uploadMultiTmpFiles(@RequestPart("files") List<MultipartFile> files) {

        List<String> responses = storageService.uploadMultiTmpFiles(files);

        return ApiResponse.<List<String>>builder()
                .result(responses)
                .build();

    }

}
