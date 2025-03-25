package com.tablelog.tablelogback.global.exception.dto;

import lombok.Builder;

@Builder
public record CustomExceptionResponseDto(
    int status,
    String name,
    String message
) {

}
