package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.transgroup.TransGroupUpdateRequest;
import com.example.mymangaapp.mymangaapp.dto.transgroup.TransGroupResponse;
import com.example.mymangaapp.mymangaapp.entity.TransGroup;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TransGroupMapper {

    TransGroupResponse toTransGroupResponse(TransGroup transGroup);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "translatedMangas", ignore = true)
    @Mapping(target = "ownedMangas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTransGroupFromRequest(@MappingTarget TransGroup transGroup, TransGroupUpdateRequest request);
}
