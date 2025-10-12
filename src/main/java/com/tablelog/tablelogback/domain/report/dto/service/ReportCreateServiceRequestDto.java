package com.tablelog.tablelogback.domain.report.dto.service;

import com.tablelog.tablelogback.global.enums.ReportType;

public record ReportCreateServiceRequestDto(
        Long reportedUserId,
        String reportContent,
        ReportType reportType,
        Long targetId
) {
}
