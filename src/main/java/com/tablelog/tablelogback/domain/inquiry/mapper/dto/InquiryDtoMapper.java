package com.tablelog.tablelogback.domain.inquiry.mapper.dto;

import com.tablelog.tablelogback.domain.inquiry.dto.controller.InquiryCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.inquiry.dto.service.InquiryCreateServiceRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface InquiryDtoMapper {
    InquiryCreateServiceRequestDto toInquiryCreateDto(
            InquiryCreateControllerRequestDto controllerDto
    );
}
