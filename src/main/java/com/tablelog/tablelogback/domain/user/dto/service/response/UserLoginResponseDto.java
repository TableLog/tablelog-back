package com.tablelog.tablelogback.domain.user.dto.service.response;

import com.tablelog.tablelogback.global.enums.UserRole;

public record UserLoginResponseDto(
        Long id,
        String email,
        String nickname,
        String name,
        String birthday,
        UserRole userRole,
        String profileImgUrl,
        String kakaoEmail,
        String googleEmail
) {
}
