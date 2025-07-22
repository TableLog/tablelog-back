package com.tablelog.tablelogback.domain.inquiry.controller;

import com.tablelog.tablelogback.domain.inquiry.dto.controller.InquiryCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.inquiry.mapper.dto.InquiryDtoMapper;
import com.tablelog.tablelogback.domain.inquiry.service.impl.InquiryServiceImpl;
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

    // 문의 생성 - 중복 유저
    // 관리자가 문의 전체 조회
    // 관리자가 문의 단건 조회
    // 유저가 문의 전체 조회
    // 유저가 문의 단건 조회
}
