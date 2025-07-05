package com.tablelog.tablelogback.domain.report.mapper.dto;

import com.tablelog.tablelogback.domain.report.dto.controller.ReportCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ReportDtoMapper {
    ReportCreateServiceRequestDto toReportCreateServiceDto(
            ReportCreateControllerRequestDto controllerRequestDto
    );
}
