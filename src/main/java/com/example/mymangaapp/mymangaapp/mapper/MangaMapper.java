package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.request.MangaRequest;
import com.example.mymangaapp.mymangaapp.dto.response.MangaResponse;
import com.example.mymangaapp.mymangaapp.dto.response.MangaSummaryResponse;
import com.example.mymangaapp.mymangaapp.entity.Manga;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MangaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerTransGroup", ignore = true)
    @Mapping(target = "transGroups", ignore = true)
    @Mapping(target = "publishedDate", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    /*
    * NullValuePropertyMappingStrategy.IGNORE: Áp dụng cho TẤT CẢ các trường còn lại (những trường KHÔNG bị ignore),
    * có nghĩa là: Nếu trong MangaCreationRequest, trường nào đó có giá trị là null, thì ĐỪNG set null vào Manga,
    * hãy giữ nguyên giá trị mặc định của Manga."
    * */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Manga toManga(MangaRequest request);

    @Mapping(target = "transGroupsId", source = "transGroups")
    @Mapping(target = "ownerTransGroupId", source = "ownerTransGroup.id")
    MangaResponse toMangaResponse(Manga manga);

    // Để mapper tự map từng phần tử transGroup thành transGroupId
    default String mapTransGroupToId(TransGroup transGroup) {
        return (transGroup != null) ? transGroup.getId() : null;
    }

    MangaSummaryResponse toMangaSummaryResponse(Manga manga);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerTransGroup", ignore = true)
    @Mapping(target = "transGroups", ignore = true)
    @Mapping(target = "publishedDate", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMangaFromRequest(@MappingTarget Manga manga, MangaRequest request);

}
