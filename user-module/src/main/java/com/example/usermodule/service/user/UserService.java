package com.example.usermodule.service.user;

import com.example.usermodule.dtos.request.UpdateUser;
import com.example.usermodule.dtos.request.UserRegister;
import com.example.usermodule.dtos.response.ApiDataResponseDto;

public interface UserService {
    ApiDataResponseDto createUser(UserRegister userDto);
    ApiDataResponseDto updateUser(UpdateUser updateUser, Long userId);
    ApiDataResponseDto getUsers(Integer page, Integer pageSize);

    ApiDataResponseDto getUser(Long userId);

    ApiDataResponseDto deleteUser(Long userId);
}