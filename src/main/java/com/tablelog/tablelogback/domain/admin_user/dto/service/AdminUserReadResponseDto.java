package com.tablelog.tablelogback.domain.admin_user.dto.service;

import com.tablelog.tablelogback.global.enums.AdminRequestType;
import com.tablelog.tablelogback.global.enums.ApplyStatus;

import java.time.LocalDateTime;

public record AdminUserReadResponseDto(
        Long id,
        Long userId,
        ApplyStatus status,
        AdminRequestType requestType,
        LocalDateTime modifiedAt,
        String rejectReason
) {
}
