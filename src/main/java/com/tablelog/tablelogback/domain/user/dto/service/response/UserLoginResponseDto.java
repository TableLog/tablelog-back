package com.tablelog.tablelogback.domain.user.dto.service.response;

import com.tablelog.tablelogback.global.enums.UserRole;

public record UserLoginResponseDto(
        Long id,
        String email,
        String nickname,
        UserRole userRole,
        String profileImgUrl,
        String kakaoEmail,
        String googleEmail
) {
}
