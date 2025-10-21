package com.tablelog.tablelogback.domain.report.dto.service;

import com.tablelog.tablelogback.global.enums.ReportTargetType;

public record ReportCreateServiceRequestDto(
        Long reportedUserId,
        String reportContent,
        ReportTargetType reportTargetType,
        Long targetId
) {
}
