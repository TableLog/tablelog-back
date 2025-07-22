package com.tablelog.tablelogback.domain.inquiry.dto.service;

import java.util.List;

public record InquirySliceReadResponseDto(
        List<InquiryReadResponseDto> contents,
        boolean hasNext
) {
}
