package com.tablelog.tablelogback.domain.user.dto.oauth2;

import com.tablelog.tablelogback.global.enums.UserProvider;

public record SocialUserInfoDto(
        String email,
        String nickname,
        String userName,
        String birthday,
        String imgUrl,
        UserProvider provider
) {
}
