package com.tablelog.tablelogback.domain.inquiry.dto.controller;

import com.tablelog.tablelogback.global.enums.InquiryType;

public record InquiryCreateControllerRequestDto(
        InquiryType inquiryType,
        String content
) {
}
