package com.tablelog.tablelogback.domain.admin_user.mapper.dto;

import com.tablelog.tablelogback.domain.admin_user.dto.controller.AdminUserRejectReasonControllerRequestDto;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserRejectReasonServiceRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AdminUserDtoMapper {
    AdminUserRejectReasonServiceRequestDto toAdminUserRejectReasonServiceRequestDto(
            AdminUserRejectReasonControllerRequestDto controllerRequestDto
    );
}
