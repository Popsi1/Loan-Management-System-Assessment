package com.example.usermodule.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageableResponseDto {

    private Integer pageNumber;

    private Integer size;

    private Object content;

    private Integer totalPages;

    private Integer totalElements;
}
