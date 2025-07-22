package com.tablelog.tablelogback.domain.inquiry.mapper.entity;

import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.inquiry.entity.Inquiry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface InquiryEntityMapper {
    @Mapping(source = "userId",target = "userId")
    Inquiry toInquiry(InquiryCreateServiceRequestDto serviceRequestDto, Long userId);
}
