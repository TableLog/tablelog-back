package com.tablelog.tablelogback.domain.user.dto.controller;

public record UpdatePasswordControllerRequestDto(
        String email,
        String newPassword
) {
}
