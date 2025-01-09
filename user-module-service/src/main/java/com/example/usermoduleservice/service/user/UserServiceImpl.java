package com.example.usermoduleservice.service.user;

import com.example.usermoduleservice.dtos.request.UpdateUser;
import com.example.usermoduleservice.dtos.request.UserRegister;
import com.example.usermoduleservice.dtos.response.ApiDataResponseDto;
import com.example.usermoduleservice.dtos.response.LoginResponseDto;
import com.example.usermoduleservice.dtos.response.PageableResponseDto;
import com.example.usermoduleservice.entity.LoanUser;
import com.example.usermoduleservice.exception.ResourceNotFoundException;
import com.example.usermoduleservice.helper.UserHelper;
import com.example.usermoduleservice.repository.UserRepository;
import com.example.usermoduleservice.util.DataResponseUtils;
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

        LoanUser loanUser = userRepository.save(userHelper.buildUserEntity(userDto));
        return DataResponseUtils.successResponse("Loan User successfully created",
                userHelper.buildUserResponseEntity(loanUser));
    }

    public ApiDataResponseDto updateUser(UpdateUser userDto, Long userId) {

        LoanUser loanUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan User not found :: " + userId));

        userHelper.updateUserEntity(loanUser, userDto);

        return DataResponseUtils.successResponse("Loan User details successfully updated",
                userHelper.buildUserResponseEntity(userRepository.save(loanUser)));
    }

    public ApiDataResponseDto getUsers(Integer page, Integer pageSize) {
        System.out.println(2);
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

        LoanUser loanUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan User not found :: " + userId));

        return DataResponseUtils.successResponse("Loan User details successfully fetched",
                userHelper.buildUserResponseEntity(loanUser));
    }

    public ApiDataResponseDto deleteUser(Long userId) {
        LoanUser loanUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan User not found :: " + userId));

        userRepository.delete(loanUser);

        return DataResponseUtils.successResponse("Loan User successfully deleted", null);
    }

    public LoginResponseDto getLoginReponseDto(LoanUser loanUser, String token){
        return LoginResponseDto.builder()
                .email(loanUser.getEmail())
                .roleName(loanUser.getRole())
                .accessToken(token)
                .build();
    }
}
