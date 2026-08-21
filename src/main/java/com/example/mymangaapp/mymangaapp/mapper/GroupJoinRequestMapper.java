package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.response.JoinRequestResponse;
import com.example.mymangaapp.mymangaapp.entity.GroupJoinRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupJoinRequestMapper {

    // Không gán toàn bộ transgroup và user vào response, chỉ cần id là đủ
    @Mapping(target = "transGroupId", source = "transGroup.id")
    @Mapping(target = "userId", source = "user.id")
    JoinRequestResponse toJoinRequestResponse(GroupJoinRequest groupJoinRequest);
}
