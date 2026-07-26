package com.example.mymangaapp.mymangaapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mymangaapp.mymangaapp.dto.request.UserCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.UserResponse;
import com.example.mymangaapp.mymangaapp.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "discord", ignore = true)
    @Mapping(target = "facebook", ignore = true)
    @Mapping(target = "memberSince", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "transGroup", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

}
