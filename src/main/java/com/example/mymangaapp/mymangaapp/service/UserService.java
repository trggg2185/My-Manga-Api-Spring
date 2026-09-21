package com.example.mymangaapp.mymangaapp.service;

import java.time.Instant;
import java.util.Set;

import com.example.mymangaapp.mymangaapp.dto.request.UserPasswordRequest;
import com.example.mymangaapp.mymangaapp.dto.response.PaginatedResponse;
import com.example.mymangaapp.mymangaapp.dto.response.UserSummaryResponse;
import com.example.mymangaapp.mymangaapp.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mymangaapp.mymangaapp.dto.request.UserCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.request.UserUpdateRequest;
import com.example.mymangaapp.mymangaapp.dto.response.UserResponse;
import com.example.mymangaapp.mymangaapp.entity.Role;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.UserMapper;
import com.example.mymangaapp.mymangaapp.repository.RoleRepository;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    // Map từ đối tượng này sang đối tượng khác nhanh chóng
    UserMapper userMapper;

    StorageService storageService;


    // --------------------------------- chức năng public đây (dành cho khách) ----------------------------------------- //

    // Tạo user mới
    @Transactional
    public UserSummaryResponse createUser(@NonNull UserCreationRequest request) {

        log.info("Create user here!------------------------------");

        // Mặc dù username có unique vẫn phải check đã tồn tại ở service
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ResponseCode.USERNAME_ALREADY_EXISTS);
        }

        // Email cũng phải check tương tự username
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }

        // Tìm role mặc định cho user mới là role USER
        Role role = roleRepository
                .findById("USER")
                .orElseThrow(() ->
                        new AppException(ResponseCode.ROLE_NOT_FOUND));

        User user = userMapper.toUser(request);

        // Mã hoá mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set role mặc định cho user mới tạo
        user.setRoles(Set.of(role));

        return userMapper.toUserSummaryResponse(userRepository.save(user));

    }


    // --------------------------------- chức năng cho user đã đăng nhập đây -----------------------------------------  //

    public UserSummaryResponse getMyInfo() {
        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        return userMapper.toUserSummaryResponse(user);
    }

    @Transactional
    public UserSummaryResponse updateMyInfo(UserUpdateRequest request) {
        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        userMapper.updateUserFromRequest(user, request);

        // nếu có cập nhật avatar cập nhật
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {

            // nếu user có avatar cũ thì xoá trc đã, nếu ko có thì thôi upload avatar mới luôn rồi set
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                String prefix = "avatars/" + user.getId() + "/";
                storageService.deleteFilesWithPrefix(prefix, Instant.now());
            }

            // sau mới upload avatar mới
            String avatarUrl = storageService.uploadAvatar(user.getId(), request.getAvatar());
            user.setAvatar(avatarUrl);

        }

        return userMapper.toUserSummaryResponse(userRepository.save(user));
    }

    @Transactional
    public void updateMyPassword(UserPasswordRequest request) {
        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ResponseCode.PASSWORD_INCORRECT);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }


    // --------------------------------- chức năng admin đây ----------------------------------------- //

    // Lấy tất cả user
    public PaginatedResponse<UserResponse> getAllUsers(
            int page, int size,
            @NonNull String sortBy
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<UserResponse> dtoPage = userRepository
                .findAll(pageable)
                .map(userMapper::toUserResponse);

        return PaginatedResponse.of(dtoPage);

    }

    // Lấy user bằng id
    public UserResponse getUserById(@NonNull String id) {
        User user = userRepository
                .findWithDetailsById(id)
                .orElseThrow(() -> 
                        new AppException(ResponseCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    // Cập nhật user bằng id
    @Transactional
    public UserResponse updateUserById(@NonNull String id, @NonNull UserUpdateRequest request) {
        
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ResponseCode.USER_NOT_FOUND));

        String newEmail = request.getEmail();

        // Request có chứa email khác null tức là email đã thay đổi
        // Phải kiểm tra email thay đổi đó có tồn tại trong db chưa
        // Nếu trong TH họ vẫn nhập email nhưng email mới và email cũ chả khác gì nhau thì thôi
        if (newEmail != null && !newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new AppException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }

        // Mapstruct tự động map từ UserUpdateRequest -> User
        userMapper.updateUserFromRequest(user, request);

        // nếu có cập nhật avatar cập nhật
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {

            // nếu user có avatar cũ thì xoá trc đã, nếu ko có thì thôi upload avatar mới luôn rồi set
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                String prefix = "avatars/" + user.getId() + "/";
                storageService.deleteFilesWithPrefix(prefix, Instant.now());
            }

            // sau mới upload avatar mới
            String avatarUrl = storageService.uploadAvatar(user.getId(), request.getAvatar());
            user.setAvatar(avatarUrl);

        }

        return userMapper.toUserResponse(userRepository.save(user));
    }


    // --------------------------- chức năng cho admin hoặc user -------------------------- //

    // Xoá user bằng id, admin xoá đc mọi user, user chỉ xoá đc chính mình
    @Transactional
    @PreAuthorize("@userSec.isSelfOrAdmin(#id)")
    public void deleteUserById(@NonNull String id) {

        if (!userRepository.existsById(id)) {
            throw new AppException(ResponseCode.USER_NOT_FOUND);
        }

        userRepository.deleteById(id);
    }

}
