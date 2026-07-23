package com.example.mymangaapp.mymangaapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    UNCATEGORIZED_ERROR(9999, "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR);
    
    int code;
    String message;
    HttpStatusCode httpStatusCode;

}
