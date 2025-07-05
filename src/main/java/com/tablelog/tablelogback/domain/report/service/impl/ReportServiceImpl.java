package com.tablelog.tablelogback.domain.report.service.impl;

import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.report.entity.Report;
import com.tablelog.tablelogback.domain.report.mapper.entity.ReportEntityMapper;
import com.tablelog.tablelogback.domain.report.repository.ReportRepository;
import com.tablelog.tablelogback.domain.report.service.ReportService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final UserRepository userRepository;
    private final ReportEntityMapper reportEntityMapper;
    private final ReportRepository reportRepository;

    @Override
    public void createReport(ReportCreateServiceRequestDto serviceRequestDto, User user){
        if(!userRepository.existsById(serviceRequestDto.reportedUserId())){
            throw new NotFoundUserException(UserErrorCode.NOT_FOUND_USER);
        }
        Report report = reportEntityMapper.toReport(serviceRequestDto, user.getId());
        reportRepository.save(report);
    }
}
