package com.example.mymangaapp.mymangaapp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.lang.NonNull;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    // Map từ đối tượng này sang đối tượng khác nhanh chóng
    UserMapper userMapper;

    // Tạo user mới
    public UserResponse createUser(UserCreationRequest request) {

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

        return userMapper.toUserResponse(userRepository.save(user));

    }

    // Lấy tất cả user
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> 
                        userMapper.toUserResponse(user))
                .toList();

    }

    // Lấy user bằng id
    public UserResponse getUserById(@NonNull String id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> 
                        new AppException(ResponseCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    // Cập nhật user bằng id
    public UserResponse updateUserById(@NonNull String id, UserUpdateRequest request) {
        
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

        if (user == null) {
            throw new AppException(ResponseCode.UNCATEGORIZED_ERROR);
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Xoá user bằng id
    public void deleteUserById(@NonNull String id) {

        if (!userRepository.existsById(id)) {
            throw new AppException(ResponseCode.USER_NOT_FOUND);
        }

        userRepository.deleteById(id);
    }

}
