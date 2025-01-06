package com.example.loanmodule.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiDataResponseDto {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private Object timestamp;
}
