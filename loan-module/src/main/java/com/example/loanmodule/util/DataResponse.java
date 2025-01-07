package com.example.loanmodule.util;

import com.example.loanmodule.dtos.response.ApiDataResponseDto;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class DataResponse {
    public static ApiDataResponseDto successResponse(String message, Object data){
        return ApiDataResponseDto.builder()
                .data(data)
                .timestamp(LocalDateTime.now())
                .message(message)
                .status(true)
                .code(HttpStatus.OK.value())
                .build();
    }
    public static ApiDataResponseDto errorResponse(String message) {
        return ApiDataResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .status(false)
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    public static ApiDataResponseDto errorResponse(String message, HttpStatus httpStatus) {
        return ApiDataResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .status(false)
                .code(httpStatus.value())
                .build();
    }
}
