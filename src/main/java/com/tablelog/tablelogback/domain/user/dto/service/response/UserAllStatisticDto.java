package com.tablelog.tablelogback.domain.user.dto.service.response;

import java.util.List;

public record UserAllStatisticDto(
        Long totalCount,
        List<UserStatisticDto> dailyCounts
) {
}
