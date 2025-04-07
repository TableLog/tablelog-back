package com.tablelog.tablelogback.domain.user.dto.oauth2;

public record KakaoUserInfoDto(
        String kakaoEmail,
        String nickname,
        String name,
        String birthday,
        String profileImgUrl
) {
}
