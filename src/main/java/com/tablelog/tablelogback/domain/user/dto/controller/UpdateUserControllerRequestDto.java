package com.tablelog.tablelogback.domain.user.dto.controller;

public record UpdateUserControllerRequestDto(
        String newEmail,
        String newPassword,
        String confirmNewPassword,
        String nickname,
        String profileImgUrl,
        Boolean imageChange
) {
}
