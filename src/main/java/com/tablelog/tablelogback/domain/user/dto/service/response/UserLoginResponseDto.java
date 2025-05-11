package com.tablelog.tablelogback.domain.user.dto.service.response;

import com.tablelog.tablelogback.domain.user.entity.OAuthAccount;
import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.enums.UserRole;

import java.util.List;

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
        Boolean marketingOptIn,
        Long recipeCount,
        Long boardCount,
        Long followerCount,
        Long followingCount,
        List<OAuthAccountResponseDto> oAuthAccounts
) {
}
