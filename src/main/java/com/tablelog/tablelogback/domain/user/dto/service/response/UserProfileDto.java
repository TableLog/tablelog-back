package com.tablelog.tablelogback.domain.user.dto.service.response;

public record UserProfileDto(
        String profileImgUrl,
        String nickname,
        Boolean isFollowed,
        Long recipeCount,
        Long boardCount
) {
}
