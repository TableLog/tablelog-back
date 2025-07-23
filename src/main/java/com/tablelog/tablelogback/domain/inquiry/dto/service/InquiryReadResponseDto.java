package com.tablelog.tablelogback.domain.inquiry.dto.service;

import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;

import java.time.LocalDateTime;

public record InquiryReadResponseDto(
        Long id,
        Long userId,
        InquiryType inquiryType,
        String content,
        ApplyStatus applyStatus,
        LocalDateTime createdAt
) {
}
