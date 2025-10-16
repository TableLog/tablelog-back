package com.tablelog.tablelogback.domain.user.dto.service.response;

import java.util.List;

public record UserProfileByAdminSliceDto(
        List<UserProfileByAdminDto> dtos,
        boolean hasNext
) {
}
