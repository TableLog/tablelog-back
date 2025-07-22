package com.tablelog.tablelogback.domain.inquiry.service.impl;

import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryReadResponseDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquirySliceReadResponseDto;
import com.tablelog.tablelogback.domain.inquiry.entity.Inquiry;
import com.tablelog.tablelogback.domain.inquiry.exception.ForbiddenAccessInquiryException;
import com.tablelog.tablelogback.domain.inquiry.exception.InquiryErrorCode;
import com.tablelog.tablelogback.domain.inquiry.exception.InvalidInquiryTypeException;
import com.tablelog.tablelogback.domain.inquiry.exception.NotFoundInquiryException;
import com.tablelog.tablelogback.domain.inquiry.mapper.entity.InquiryEntityMapper;
import com.tablelog.tablelogback.domain.inquiry.repository.InquiryRepository;
import com.tablelog.tablelogback.domain.inquiry.service.InquiryService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;
import com.tablelog.tablelogback.global.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public InquiryReadResponseDto readInquiry(Long inquiryId, User user){
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NotFoundInquiryException(InquiryErrorCode.NOT_FOUND_INQUIRY));
        if (user.getUserRole() != UserRole.ADMIN && !user.getId().equals(inquiry.getUserId())) {
            throw new ForbiddenAccessInquiryException(InquiryErrorCode.FORBIDDEN_ACCESS_INQUIRY);
        }
        return inquiryEntityMapper.toInquiryReadResponseDto(inquiry);
    }

    @Override
    public InquirySliceReadResponseDto readAllInquiriesByUser(
            ApplyStatus applyStatus, InquiryType inquiryType, int pageNum, User user
    ){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Inquiry> slice = inquiryRepository.findAllByUserWithOptionalFilters(
                user.getId(), applyStatus, inquiryType, pageRequest);
        List<InquiryReadResponseDto> inquiries = inquiryEntityMapper.toInquiryReadAllResponseDto(slice.getContent());
        return new InquirySliceReadResponseDto(inquiries, slice.hasNext());
    }

    @Override
    public InquirySliceReadResponseDto readAllInquiriesByAdmin(
            ApplyStatus applyStatus, InquiryType inquiryType, int pageNum
    ){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Inquiry> slice = inquiryRepository.findAllByAdminWithOptionalFilters(applyStatus, inquiryType, pageRequest);
        List<InquiryReadResponseDto> inquiries = inquiryEntityMapper.toInquiryReadAllResponseDto(slice.getContent());
        return new InquirySliceReadResponseDto(inquiries, slice.hasNext());
    }
}
