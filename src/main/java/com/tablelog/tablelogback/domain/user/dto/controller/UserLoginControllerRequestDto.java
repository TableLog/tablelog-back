package com.tablelog.tablelogback.domain.user.dto.controller;

public record UserLoginControllerRequestDto(
        String email,
        String password
) {
}
