package com.tablelog.tablelogback.domain.user.dto.service.request;

public record UserSignUpServiceRequestDto(
        String email,
        String password,
        String confirmPassword,
        String nickname,
        String name,
        String birthday
) {
}
