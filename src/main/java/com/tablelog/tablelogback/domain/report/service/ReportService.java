package com.tablelog.tablelogback.domain.report.service;

import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportReadResponseDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportSliceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.ApplyStatus;

public interface ReportService {
    void createReport(ReportCreateServiceRequestDto serviceRequestDto, User user);
    ReportReadResponseDto getReport(Long id);

    ReportSliceResponseDto getAllReports(ApplyStatus status, int pageNum);

    void approveReport(Long id);

    void rejectReport(Long id);

    void processingReport(Long id);
}
