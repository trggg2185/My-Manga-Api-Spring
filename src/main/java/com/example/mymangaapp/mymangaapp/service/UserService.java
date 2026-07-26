package com.example.mymangaapp.mymangaapp.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mymangaapp.mymangaapp.dto.request.UserCreationRequest;
import com.example.mymangaapp.mymangaapp.dto.response.UserResponse;
import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.example.mymangaapp.mymangaapp.mapper.UserMapper;
import com.example.mymangaapp.mymangaapp.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    public UserResponse createUser(UserCreationRequest request) {

        // Mặc dù username có unique vẫn phải check đã tồn tại ở service
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ResponseCode.USERNAME_ALREADY_EXISTS);
        }

        // Email cũng phải check tương tự username
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toUser(request);

        // Mã hoá mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> 
                        userMapper.toUserResponse(user))
                                .toList();
    }

    public void deleteUserById(String id) {

        if (!userRepository.existsById(id)) {
            throw new AppException(ResponseCode.USER_NOT_EXISTS);
        }

        userRepository.deleteById(id);
    }

}
