package com.tablelog.tablelogback.domain.follow.dto;

import java.util.List;

public record FollowUserListDto(
        List<FollowUserDto> users,
        Boolean hasNext
) {
}
