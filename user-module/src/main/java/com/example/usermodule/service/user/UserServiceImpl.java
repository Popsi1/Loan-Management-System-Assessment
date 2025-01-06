package com.example.usermodule.service.user;

import com.example.usermodule.dtos.request.UpdateUser;
import com.example.usermodule.dtos.request.UserRegister;
import com.example.usermodule.dtos.response.ApiDataResponseDto;
import com.example.usermodule.dtos.response.PageableResponseDto;
import com.example.usermodule.entity.User;
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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    UserHelper userHelper;

    public ApiDataResponseDto createUser(UserRegister userDto) {

        User user = userRepository.save(userHelper.buildUserEntity(userDto));
        return DataResponseUtils.successResponse("User successfully created",
                userHelper.buildUserResponseEntity(user));
    }

    public ApiDataResponseDto updateUser(UpdateUser userDto, Long userId) {

        User user = userRepository.findUserByIdAndDeleted(userId, false);
        if (Objects.isNull(user)) throw new ResourceNotFoundException("User does not exist");

        return DataResponseUtils.successResponse("User details successfully updated",
                userHelper.buildUserResponseEntity(userRepository.save(userHelper.buildUserEntity(user, userDto))));
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
        User user = userRepository.findUserByIdAndDeleted(userId, false);
        if (Objects.isNull(user)) throw new ResourceNotFoundException("User does not exist");

        return DataResponseUtils.successResponse("User details successfully fetched",
                userHelper.buildUserResponseEntity(user));
    }

    public ApiDataResponseDto deleteUser(Long userId) {
        User user = userRepository.findUserByIdAndDeleted(userId, false);
        if (Objects.isNull(user)) throw new ResourceNotFoundException("User does not exist");
        user.setDeleted(true);

        return DataResponseUtils.successResponse("User successfully deleted",
                userHelper.buildUserResponseEntity(userRepository.save(user)));
    }

}
