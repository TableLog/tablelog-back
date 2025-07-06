package com.tablelog.tablelogback.domain.report.service.impl;

import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportReadResponseDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportSliceResponseDto;
import com.tablelog.tablelogback.domain.report.entity.Report;
import com.tablelog.tablelogback.domain.report.exception.NotFoundReportException;
import com.tablelog.tablelogback.domain.report.exception.ReportErrorCode;
import com.tablelog.tablelogback.domain.report.mapper.entity.ReportEntityMapper;
import com.tablelog.tablelogback.domain.report.repository.ReportRepository;
import com.tablelog.tablelogback.domain.report.service.ReportService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public ReportReadResponseDto getReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundReportException(ReportErrorCode.NOT_FOUND_REPORT));
        return reportEntityMapper.toReportReadResponseDto(report);
    }

    @Override
    public ReportSliceResponseDto getAllReports(ApplyStatus status, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Report> slice = null;
        if(status == null){
            slice = reportRepository.findAll(pageRequest);
        } else {
            slice = reportRepository.findAllByStatus(status, pageRequest);
        }
        List<ReportReadResponseDto> reports = reportEntityMapper.toReportReadAllResponseDto(slice.getContent());
        return new ReportSliceResponseDto(reports, slice.hasNext());
    }

    @Override
    public void approveReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundReportException(ReportErrorCode.NOT_FOUND_REPORT));
        report.approve();
        reportRepository.save(report);
    }

    @Override
    public void rejectReport(Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundReportException(ReportErrorCode.NOT_FOUND_REPORT));
        report.reject();
        reportRepository.save(report);
    }
}
