package com.tablelog.tablelogback.global.exception.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record BeanValidationExceptionResponseDto(
    int status,
    List<FieldErrorResponseDto> messages
) {

}
