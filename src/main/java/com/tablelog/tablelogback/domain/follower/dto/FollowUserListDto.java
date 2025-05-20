package com.tablelog.tablelogback.domain.follower.dto;

import java.util.List;

public record FollowUserListDto(
        List<FollowUserDto> users,
        Boolean hasNext
) {
}
