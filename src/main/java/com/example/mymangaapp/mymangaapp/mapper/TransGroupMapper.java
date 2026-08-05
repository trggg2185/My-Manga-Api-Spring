package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.request.TransGroupCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransGroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "mangas", ignore = true)
    @Mapping(target = "foundedDate", ignore = true)
    TransGroup toTransGroup(TransGroupCreationRequest request);

    TransGroupResponse toTransGroupResponse(TransGroup transGroup);
}
