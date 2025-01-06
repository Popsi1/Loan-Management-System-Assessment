package com.example.usermodule.service.user;

import com.example.usermodule.dtos.request.UpdateUser;
import com.example.usermodule.dtos.request.UserRegister;
import com.example.usermodule.dtos.response.ApiDataResponseDto;
import com.example.usermodule.dtos.response.LoginResponseDto;
import com.example.usermodule.dtos.response.PageableResponseDto;
import com.example.usermodule.entity.LoanUser;
import com.example.usermodule.exception.ResourceNotFoundException;
import com.example.usermodule.helper.UserHelper;
import com.example.usermodule.repository.UserRepository;
import com.example.usermodule.util.DataResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    UserHelper userHelper;

    public ApiDataResponseDto createUser(UserRegister userDto) {

        System.out.println(1);
        LoanUser loanUser = userRepository.save(userHelper.buildUserEntity(userDto));
        System.out.println(2);
        System.out.println(loanUser);
        System.out.println(3);
        return DataResponseUtils.successResponse("LoanUser successfully created",
                userHelper.buildUserResponseEntity(loanUser));
    }

    public ApiDataResponseDto updateUser(UpdateUser userDto, Long userId) {

        LoanUser loanUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LoanUser not found :: " + userId));

        return DataResponseUtils.successResponse("LoanUser details successfully updated",
                userHelper.buildUserResponseEntity(userRepository.save(userHelper.buildUserEntity(loanUser, userDto))));
    }

    public ApiDataResponseDto getUsers(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return CompletableFuture.supplyAsync(() -> userRepository.allUsers(pageable))
                .thenApplyAsync(page1 -> {
                    PageableResponseDto data = PageableResponseDto.builder()
                            .totalPages(page1.getTotalPages())
                            .totalElements((int) page1.getTotalElements())
                            .pageNumber(page1.getNumber())
                            .size(page1.getSize())
                            .content(page1.getContent().stream().map(userHelper::buildUserResponseEntity).toList())
                            .build();
                    return DataResponseUtils.successResponse("Retrieved all users", data);
                }).exceptionallyAsync(DataResponseUtils::errorResponse).join();
    }

    public ApiDataResponseDto getUser(Long userId) {

        System.out.println(111);
        LoanUser loanUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LoanUser not found :: " + userId));

        System.out.println(222);

        return DataResponseUtils.successResponse("LoanUser details successfully fetched",
                userHelper.buildUserResponseEntity(loanUser));
    }

    public ApiDataResponseDto deleteUser(Long userId) {
        LoanUser loanUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LoanUser not found :: " + userId));

        userRepository.delete(loanUser);

        return DataResponseUtils.successResponse("LoanUser successfully deleted", null);
    }

    public LoginResponseDto getLoginReponseDto(String email, String token){
        LoanUser loanUser = userRepository.findUserByEmail(email);
        return LoginResponseDto.builder()
                .email(email)
                .roleName(loanUser.getRole())
                .accessToken(token)
                .build();
    }
}
