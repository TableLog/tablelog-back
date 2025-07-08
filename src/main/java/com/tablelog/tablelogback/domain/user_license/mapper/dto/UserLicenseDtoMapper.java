package com.tablelog.tablelogback.domain.user_license.mapper.dto;

import com.tablelog.tablelogback.domain.user_license.dto.controller.UserLicenseCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserLicenseDtoMapper {
    UserLicenseCreateServiceRequestDto toUserLicenseCreateServiceDto(
            UserLicenseCreateControllerRequestDto controllerRequestDto
    );
}
