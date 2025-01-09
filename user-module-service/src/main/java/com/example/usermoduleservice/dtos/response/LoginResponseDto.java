package com.example.usermoduleservice.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    private String email;

    private String accessToken;

    private String roleName;

}
