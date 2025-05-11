package com.tablelog.tablelogback.domain.user.dto.controller;

public record UpdateUserControllerRequestDto(
        String email,
        String password,
        String nickname,
        String profileImgUrl,
        Boolean marketingOptIn
) {
}
