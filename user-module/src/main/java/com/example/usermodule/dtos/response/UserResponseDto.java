package com.example.usermodule.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    private String role;

    private String createdAt;

    private String updatedAt;

    private boolean isDeleted;
}
