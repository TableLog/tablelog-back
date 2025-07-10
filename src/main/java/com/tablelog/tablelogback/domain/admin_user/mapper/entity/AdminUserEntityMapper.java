package com.tablelog.tablelogback.domain.admin_user.mapper.entity;

import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserReadResponseDto;
import com.tablelog.tablelogback.domain.admin_user.entity.AdminUser;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AdminUserEntityMapper {
    List<AdminUserReadResponseDto> toAdminUserResponseDto(List<AdminUser> adminUsers);
}
