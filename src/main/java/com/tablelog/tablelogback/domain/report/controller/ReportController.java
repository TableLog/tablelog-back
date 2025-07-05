package com.tablelog.tablelogback.domain.report.controller;

import com.tablelog.tablelogback.domain.report.dto.controller.ReportCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.report.mapper.dto.ReportDtoMapper;
import com.tablelog.tablelogback.domain.report.service.impl.ReportServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "신고 API", description = "")
public class ReportController {
    private final ReportDtoMapper reportDtoMapper;
    private final ReportServiceImpl reportService;

    @Operation(summary = "신고 생성")
    @PostMapping("/report")
    public ResponseEntity<?> createReport(
            @RequestBody ReportCreateControllerRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ReportCreateServiceRequestDto serviceRequestDto = reportDtoMapper.toReportCreateServiceDto(requestDto);
        reportService.createReport(serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
