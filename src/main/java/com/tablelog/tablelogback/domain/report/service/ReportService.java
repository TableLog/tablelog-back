package com.tablelog.tablelogback.domain.report.service;

import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;

public interface ReportService {
    void createReport(ReportCreateServiceRequestDto serviceRequestDto, User user);
}
