package com.tablelog.tablelogback.domain.inquiry.dto.service;

import com.tablelog.tablelogback.global.enums.InquiryType;

public record InquiryCreateServiceRequestDto(
        InquiryType inquiryType,
        String content
) {
}
