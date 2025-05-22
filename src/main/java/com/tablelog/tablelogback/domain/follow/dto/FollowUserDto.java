package com.tablelog.tablelogback.domain.follow.dto;

public record FollowUserDto(
        Long userId,
        String nickname,
        String profileImgUrl,
        Boolean isFollowed
) {
}
