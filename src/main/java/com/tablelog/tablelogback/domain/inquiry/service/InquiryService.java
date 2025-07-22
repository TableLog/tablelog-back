package com.tablelog.tablelogback.domain.inquiry.service;

import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryReadResponseDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquirySliceReadResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;

public interface InquiryService {
    void createInquiryAboutFood(InquiryCreateServiceRequestDto serviceRequestDto, User user);
    InquiryReadResponseDto readInquiry(Long inquiryId, User user);
    InquirySliceReadResponseDto readAllInquiriesByUser(
            ApplyStatus applyStatus, InquiryType inquiryType, int pageNum, User user);
    InquirySliceReadResponseDto readAllInquiriesByAdmin(
            ApplyStatus applyStatus, InquiryType inquiryType, int pageNum);
    void deleteInquiry(Long inquiryId, User user);
}
