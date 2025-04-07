package com.tablelog.tablelogback.domain.user.dto.controller;

public record UserSignUpControllerRequestDto(
        String email,
        String password,
        String confirmPassword,
        String nickname,
        String name,
        String birthday
) {
}
