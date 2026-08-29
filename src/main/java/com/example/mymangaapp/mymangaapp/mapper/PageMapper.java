package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.response.PageResponse;
import com.example.mymangaapp.mymangaapp.entity.Page;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PageMapper {

    PageResponse toPageResponse(Page page);

}
