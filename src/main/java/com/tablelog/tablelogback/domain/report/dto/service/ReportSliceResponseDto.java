package com.tablelog.tablelogback.domain.report.dto.service;

import java.util.List;

public record ReportSliceResponseDto(
        List<ReportReadResponseDto> contents,
        boolean hasNext
) {
}
