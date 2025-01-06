package com.example.usermodule.helper;

import com.example.usermodule.dtos.request.UpdateUser;
import com.example.usermodule.dtos.request.UserRegister;
import com.example.usermodule.dtos.response.UserResponseDto;
import com.example.usermodule.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserHelper {

    private final PasswordEncoder passwordEncoder;

    public User buildUserEntity(UserRegister userDto){
        return User.builder()
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .role(userDto.getRole())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .deleted(false)
                .build();
    }

    public User buildUserEntity(User user, UpdateUser userDto){
        return User.builder()
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .role(userDto.getRole())
                .name(userDto.getName())
                .build();
    }

    public UserResponseDto buildUserResponseEntity(User user){
        return UserResponseDto.builder()
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .name(user.getName())
                .id(user.getId())
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .isDeleted(user.isDeleted())
                .build();
    }
}
