package com.tablelog.tablelogback.domain.report.mapper.entity;

import com.tablelog.tablelogback.domain.report.dto.service.ReportCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.report.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ReportEntityMapper {
    @Mapping(source = "reporterId",target = "reporterId")
    Report toReport(ReportCreateServiceRequestDto requestDto, Long reporterId);
}
