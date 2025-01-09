package com.example.usermoduleservice.service.user;


import com.example.usermoduleservice.dtos.request.UpdateUser;
import com.example.usermoduleservice.dtos.request.UserRegister;
import com.example.usermoduleservice.dtos.response.ApiDataResponseDto;
import com.example.usermoduleservice.dtos.response.LoginResponseDto;
import com.example.usermoduleservice.entity.LoanUser;

public interface UserService {
    ApiDataResponseDto createUser(UserRegister userDto);
    ApiDataResponseDto updateUser(UpdateUser updateUser, Long userId);
    ApiDataResponseDto getUsers(Integer page, Integer pageSize);

    ApiDataResponseDto getUser(Long userId);

    ApiDataResponseDto deleteUser(Long userId);
    public LoginResponseDto getLoginReponseDto(LoanUser loanUser, String token);
}