package com.tablelog.tablelogback.domain.report.dto.service;

import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.ReportTargetType;

public record ReportReadResponseDto(
        Long id,
        Long reporterId,
        Long reportedUserId,
        String reportContent,
        ReportTargetType reportTargetType,
        ApplyStatus status,
        Long targetId
) {
}
