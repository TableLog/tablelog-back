package com.tablelog.tablelogback.domain.report.dto.controller;

import com.tablelog.tablelogback.global.enums.ReportType;

public record ReportCreateControllerRequestDto(
        Long reportedUserId,
        String reportContent,
        ReportType reportType,
        Long targetId
) {
}
