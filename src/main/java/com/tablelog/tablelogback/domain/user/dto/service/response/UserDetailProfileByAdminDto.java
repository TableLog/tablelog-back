package com.tablelog.tablelogback.domain.user.dto.service.response;

import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public record UserDetailProfileByAdminDto(
        Long id,
        String userName,
        UserRole userRole,
        String email,
        String nickname,
        LocalDateTime createdAt,
        UserProvider provider,
        List<OAuthAccountResponseDto> oAuthAccounts,
        String profileImgUrl,
        String birthday,
        LocalDateTime expertAt
) {
}
