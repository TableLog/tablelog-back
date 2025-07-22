package com.tablelog.tablelogback.domain.inquiry.service.impl;

import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.inquiry.entity.Inquiry;
import com.tablelog.tablelogback.domain.inquiry.exception.InquiryErrorCode;
import com.tablelog.tablelogback.domain.inquiry.exception.InvalidInquiryTypeException;
import com.tablelog.tablelogback.domain.inquiry.mapper.entity.InquiryEntityMapper;
import com.tablelog.tablelogback.domain.inquiry.repository.InquiryRepository;
import com.tablelog.tablelogback.domain.inquiry.service.InquiryService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InquiryServiceImpl implements InquiryService {
    private final InquiryEntityMapper inquiryEntityMapper;
    private final InquiryRepository inquiryRepository;

    @Override
    public void createInquiryAboutFood(InquiryCreateServiceRequestDto serviceRequestDto, User user){
        if(serviceRequestDto.inquiryType() != InquiryType.FOOD){
            throw new InvalidInquiryTypeException(InquiryErrorCode.INVALID_INQUIRY_TYPE);
        }
        Inquiry inquiry = inquiryEntityMapper.toInquiry(serviceRequestDto, user.getId());
        inquiryRepository.save(inquiry);
    }
}
