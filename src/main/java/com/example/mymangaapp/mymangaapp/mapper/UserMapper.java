package com.example.mymangaapp.mymangaapp.mapper;

import com.example.mymangaapp.mymangaapp.dto.response.UserSummaryResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.mymangaapp.mymangaapp.dto.request.UserCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.request.UserUpdateRequest;
import com.example.mymangaapp.mymangaapp.dto.response.UserResponse;
import com.example.mymangaapp.mymangaapp.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /*
        - NullValuePropertyMappingStrategy.IGNORE: Dùng để MapStruct không ghi đè
          null lên các trường hợp lệ (fullName, phone, avatarUrl...).

        - @Mapping(target = "...", ignore = true): Dùng để khoá chặt các trường
          nhạy cảm (id, role, password, email...) không cho phép cập nhật tự động qua form update này.
    */
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "transGroup", ignore = true)
    @Mapping(target = "memberSince", ignore = true)
    @Mapping(target = "facebook", ignore = true)
    @Mapping(target = "discord", ignore = true)
    @Mapping(target = "bio", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(target = "id", ignore = true) // Không sửa id
    @Mapping(target = "username", ignore = true) // Không sửa username
    @Mapping(target = "password", ignore = true) // Sửa password ở endpoint riêng, ko ở update
    @Mapping(target = "memberSince", ignore = true) // Không sửa memberSince
    @Mapping(target = "transGroup", ignore = true) // tạmthời lờ đi transGroup
    @Mapping(target = "roles", ignore = true) // roles ta tự giải quyết bên user service
    // Dùng NullValuePropertyMappingStrategy.IGNORE để nếu request field = null thì BỎ QUA không ghi đè
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(target = "transGroupId", source = "transGroup.id")
    UserResponse toUserResponse(User user);

    UserSummaryResponse toUserSummaryResponse(User user);

}
