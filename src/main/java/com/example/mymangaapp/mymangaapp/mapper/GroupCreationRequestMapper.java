package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.request.CreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.CreationRequestResponse;
import com.example.mymangaapp.mymangaapp.entity.GroupCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupCreationRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "status", ignore = true)
    GroupCreationRequest toGroupCreationRequest(CreationRequest request);

    @Mapping(target = "creatorId", source = "creator.id")
    CreationRequestResponse toCreationRequestResponse(GroupCreationRequest groupCreationRequest);
}
