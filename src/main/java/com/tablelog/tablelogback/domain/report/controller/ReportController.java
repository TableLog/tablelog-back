package com.tablelog.tablelogback.domain.report.controller;

import com.tablelog.tablelogback.domain.report.dto.controller.ReportCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportReadResponseDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportSliceResponseDto;
import com.tablelog.tablelogback.domain.report.mapper.dto.ReportDtoMapper;
import com.tablelog.tablelogback.domain.report.service.impl.ReportServiceImpl;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "신고 API", description = "")
public class ReportController {
    private final ReportDtoMapper reportDtoMapper;
    private final ReportServiceImpl reportService;

    @Operation(summary = "신고 생성")
    @PostMapping("/reports")
    public ResponseEntity<?> createReport(
            @RequestBody ReportCreateControllerRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ReportCreateServiceRequestDto serviceRequestDto = reportDtoMapper.toReportCreateServiceDto(requestDto);
        reportService.createReport(serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "신고 단건 조회")
    @GetMapping("/admin/reports/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportReadResponseDto> getReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ReportReadResponseDto responseDto = reportService.getReport(reportId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리자가 신고 전체 조회 By status")
    @GetMapping("/admin/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportSliceResponseDto> getAllReports(
            @RequestParam(required = false) ApplyStatus status,
            @RequestParam int pageNum,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ReportSliceResponseDto responseDto = reportService.getAllReports(status, pageNum);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리자가 신고 승인")
    @PostMapping("/admin/reports/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        reportService.approveReport(reportId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // 관리자가 신고 거절
}
