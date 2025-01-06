package com.example.usermodule.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    private String email;

    private String accessToken;

    private String roleName;

}
