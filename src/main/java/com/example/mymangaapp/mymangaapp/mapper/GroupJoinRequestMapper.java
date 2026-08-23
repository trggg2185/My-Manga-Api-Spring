package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.response.JoinRequestResponse;
import com.example.mymangaapp.mymangaapp.entity.GroupJoinRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupJoinRequestMapper {

    // Không gán toàn bộ transgroup và user vào response, chỉ cần name là đủ
    @Mapping(target = "transGroupName", source = "transGroup.name")
    @Mapping(target = "username", source = "user.username")
    JoinRequestResponse toJoinRequestResponse(GroupJoinRequest groupJoinRequest);
}
