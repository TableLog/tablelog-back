package com.tablelog.tablelogback.domain.report.dto.service;

import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.ReportTargetType;

import java.time.LocalDateTime;

public record ReportReadResponseDto(
        Long id,
        Long reporterId,
        Long reportedUserId,
        String reporterNickname,
        String reportedNickname,
        String reportContent,
        ReportTargetType reportTargetType,
        Long targetId,
        ApplyStatus status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
}
