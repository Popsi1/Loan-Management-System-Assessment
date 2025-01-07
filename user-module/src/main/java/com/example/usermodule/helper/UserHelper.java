package com.example.usermodule.helper;

import com.example.usermodule.dtos.request.UpdateUser;
import com.example.usermodule.dtos.request.UserRegister;
import com.example.usermodule.dtos.response.UserResponseDto;
import com.example.usermodule.entity.LoanUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserHelper {

    private final PasswordEncoder passwordEncoder;

    public LoanUser buildUserEntity(UserRegister userDto){
        return LoanUser.builder()
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .role(userDto.getRole())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateUserEntity(LoanUser loanUser, UpdateUser userDto){
        loanUser.setEmail(userDto.getEmail());
        loanUser.setPhoneNumber(userDto.getPhoneNumber());
        loanUser.setRole(userDto.getRole());
        loanUser.setName(userDto.getName());
        loanUser.setUpdatedAt(LocalDateTime.now());
    }

    public UserResponseDto buildUserResponseEntity(LoanUser loanUser){
        return UserResponseDto.builder()
                .email(loanUser.getEmail())
                .phoneNumber(loanUser.getPhoneNumber())
                .role(loanUser.getRole())
                .name(loanUser.getName())
                .id(loanUser.getId())
                .createdAt(loanUser.getCreatedAt().toString())
                .updatedAt(loanUser.getUpdatedAt().toString())
                .isDeleted(loanUser.isDeleted())
                .build();
    }
}
