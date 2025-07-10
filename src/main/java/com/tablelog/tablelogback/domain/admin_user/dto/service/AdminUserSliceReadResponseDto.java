package com.tablelog.tablelogback.domain.admin_user.dto.service;

import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserReadResponseDto;

import java.util.List;

public record AdminUserSliceReadResponseDto(
        List<AdminUserReadResponseDto> contents,
        boolean hasNext
) {
}
