package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.request.MangaCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.entity.Manga;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MangaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transGroups", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "publishedDate", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    /*
    * NullValuePropertyMappingStrategy.IGNORE: Áp dụng cho TẤT CẢ các trường còn lại (những trường KHÔNG bị ignore),
    * có nghĩa là: Nếu trong MangaCreationRequest, trường nào đó có giá trị là null, thì ĐỪNG set null vào Manga,
    * hãy giữ nguyên giá trị mặc định của Manga."
    * */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Manga toManga(MangaCreationRequest request);

    MangaResponse toMangaResponse(Manga manga);

}
