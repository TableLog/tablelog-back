package com.tablelog.tablelogback.domain.inquiry.mapper.entity;

import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryReadResponseDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquirySliceReadResponseDto;
import com.tablelog.tablelogback.domain.inquiry.entity.Inquiry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface InquiryEntityMapper {
    @Mapping(source = "userId",target = "userId")
    Inquiry toInquiry(InquiryCreateServiceRequestDto serviceRequestDto, Long userId);

    InquiryReadResponseDto toInquiryReadResponseDto(Inquiry inquiry);

    List<InquiryReadResponseDto> toInquiryReadAllResponseDto(List<Inquiry> inquiries);
}
