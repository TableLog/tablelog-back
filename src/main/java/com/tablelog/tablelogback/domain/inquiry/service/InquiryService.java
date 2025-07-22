package com.tablelog.tablelogback.domain.inquiry.service;

import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;

public interface InquiryService {
    void createInquiryAboutFood(InquiryCreateServiceRequestDto serviceRequestDto, User user);
}
