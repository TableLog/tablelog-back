package com.tablelog.tablelogback.domain.user.dto.service.response;

import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.enums.UserRole;

public record UserLoginResponseDto(
        Long id,
        String email,
        String nickname,
        String userName,
        String birthday,
        UserRole userRole,
        String profileImgUrl,
        UserProvider provider,
        Integer pointBalance,
        Boolean marketingOptIn
) {
}
