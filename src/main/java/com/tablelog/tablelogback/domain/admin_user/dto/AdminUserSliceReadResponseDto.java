package com.tablelog.tablelogback.domain.admin_user.dto;

import java.util.List;

public record AdminUserSliceReadResponseDto(
        List<AdminUserReadResponseDto> contents,
        boolean hasNext
) {
}
