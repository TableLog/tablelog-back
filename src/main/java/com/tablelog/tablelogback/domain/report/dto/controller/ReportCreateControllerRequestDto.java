package com.tablelog.tablelogback.domain.report.dto.controller;

import com.tablelog.tablelogback.global.enums.ReportTargetType;

public record ReportCreateControllerRequestDto(
        Long reportedUserId,
        String reportContent,
        ReportTargetType reportTargetType,
        Long targetId
) {
}
