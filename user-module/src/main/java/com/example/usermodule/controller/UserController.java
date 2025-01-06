package com.example.usermodule.controller;

import com.example.usermodule.config.securityConfig.JWTUtility;
import com.example.usermodule.dtos.request.LoginRequest;
import com.example.usermodule.dtos.request.UpdateUser;
import com.example.usermodule.dtos.request.UserRegister;
import com.example.usermodule.dtos.response.ApiDataResponseDto;
import com.example.usermodule.dtos.response.LoginResponseDto;
import com.example.usermodule.service.MyUserDetailsService;
import com.example.usermodule.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "LoanUser Route", description = "LoanUser Account Management API documentation")
public class UserController {
    private final UserService userService;
    private final MyUserDetailsService myUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtility jwtUtility;

    @PostMapping("/signup")
    @Operation(summary = "Create a LoanUser Account")
    public ResponseEntity<ApiDataResponseDto> createUser(@Valid @RequestBody UserRegister userRegister) {
        return new ResponseEntity<>(userService.createUser(userRegister), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Update a LoanUser Account")
    public ResponseEntity<ApiDataResponseDto> updateUser(@Valid @RequestBody UpdateUser updateUser, @PathVariable Long userId) {
        return new ResponseEntity<>(userService.updateUser(updateUser, userId), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all LoanUser Account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponseDto> getUsers(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return new ResponseEntity<>(userService.getUsers(page, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a LoanUser Account")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponseDto> getUser(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a LoanUser Account")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponseDto> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "LoanUser Login Account")
    public ResponseEntity<ApiDataResponseDto> userLogin(LoginRequest loginRequest) {
        UserDetails userDetails = null;
        try {
            userDetails = myUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        } catch (Exception e) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(false, 404, null, e.getMessage(), LocalDateTime.now());
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.NOT_FOUND);
        }

        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(false, 404, null, e.getMessage(), LocalDateTime.now());
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        final String token = jwtUtility.generateToken(userDetails);

        LoginResponseDto loginResponseDto = userService.getLoginReponseDto(loginRequest.getEmail(), token);

        ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(true, 200, loginResponseDto, "user successfully login", LocalDateTime.now());
        return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
    }


}