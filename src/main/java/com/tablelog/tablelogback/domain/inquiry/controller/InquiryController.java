package com.tablelog.tablelogback.domain.inquiry.controller;

import com.tablelog.tablelogback.domain.inquiry.dto.controller.InquiryCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryReadResponseDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquirySliceReadResponseDto;
import com.tablelog.tablelogback.domain.inquiry.mapper.dto.InquiryDtoMapper;
import com.tablelog.tablelogback.domain.inquiry.service.impl.InquiryServiceImpl;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;
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
@Tag(name = "문의 API", description = "")
public class InquiryController {
    private final InquiryDtoMapper inquiryDtoMapper;
    private final InquiryServiceImpl inquiryService;

    @Operation(summary = "문의 생성 - 재료 추가")
    @PostMapping("/inquiries")
    public ResponseEntity<?> createInquiryAboutFood(
            @RequestBody InquiryCreateControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails

    ){
        InquiryCreateServiceRequestDto serviceRequestDto = inquiryDtoMapper.toInquiryCreateDto(controllerRequestDto);
        inquiryService.createInquiryAboutFood(serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "문의 단건 조회", description = "관리자나 작성자만 조회 가능")
    @GetMapping("/inquiries/{inquiryId}")
    public ResponseEntity<?> readInquiry(
            @PathVariable Long inquiryId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        InquiryReadResponseDto responseDto = inquiryService.readInquiry(inquiryId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    @Operation(summary = "관리자가 문의 전체 조회 By Status And Type")
//    @GetMapping("/admin/inquiries")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> readAllInquiries(
//            @RequestParam(required = false) ApplyStatus applyStatus,
//            @RequestParam(required = false) InquiryType inquiryType,
//            @RequestParam int pageNum,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ){
//        InquirySliceReadResponseDto responseDto = inquiryService.getAllInquiries(applyStatus, inquiryType, pageNum);
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }

    // 문의 생성 - 중복 유저
    // 관리자가 문의 전체 조회
    // 관리자가 문의 단건 조회
    // 유저가 문의 전체 조회
    // 유저가 문의 단건 조회
}
