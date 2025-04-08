package com.tablelog.tablelogback.domain.user.dto.oauth2;

public record GoogleUserInfoDto(
        String googleEmail,
        String nickname,
        String name,
        String birthday,
        String profileImgUrl
) {
}
