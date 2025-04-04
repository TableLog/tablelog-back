package com.tablelog.tablelogback.domain.user.dto.controller;

public record UpdateUserControllerRequestDto(
        String newPassword,
        String confirmNewPassword,
        String nickname,
        String profileImgUrl,
        Boolean ImageChange,
        String kakaoEmail,
        String googleEmail
) {
}
