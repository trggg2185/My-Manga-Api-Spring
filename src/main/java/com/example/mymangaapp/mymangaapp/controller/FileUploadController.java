package com.example.mymangaapp.mymangaapp.controller;

import com.example.mymangaapp.mymangaapp.dto.response.ApiResponse;
import com.example.mymangaapp.mymangaapp.service.StorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileUploadController {

    StorageService storageService;


    // -------------------------------endpoints cho admin và translaor ----------------------------//

    // request part là lấy data từ form-data trong request
    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadTmpFile(@RequestPart("file") MultipartFile file) {

        String response = storageService.uploadTmpFile(file);

        return ApiResponse.<String>builder()
                .result(response)
                .build();
    }

    @PostMapping(value = "/files/upload-multi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<String>> uploadMultiTmpFiles(@RequestPart("files") @NonNull List<MultipartFile> files) {

        List<String> responses = storageService.uploadMultiTmpFiles(files);

        return ApiResponse.<List<String>>builder()
                .result(responses)
                .build();

    }

}
