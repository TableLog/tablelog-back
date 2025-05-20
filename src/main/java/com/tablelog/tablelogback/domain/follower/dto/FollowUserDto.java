package com.tablelog.tablelogback.domain.follower.dto;

public record FollowUserDto(
        Long userId,
        String nickname,
        Boolean isFollowed
) {
}
