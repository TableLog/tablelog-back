package com.tablelog.tablelogback.domain.user.dto.service.request;

public record UserLoginServiceRequestDto(
        String email,
        String password
) {
}
