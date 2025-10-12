package com.tablelog.tablelogback.domain.user_license.mapper.entity;

import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCountResponseDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseReadResponseDto;
import com.tablelog.tablelogback.domain.user_license.entity.UserLicense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserLicenseEntityMapper {
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "imageUrl", target = "imageUrl")
    UserLicense toUserLicense(UserLicenseCreateServiceRequestDto requestDto, Long userId, String imageUrl);

    UserLicenseReadResponseDto toUserLicenseReadResponseDto(UserLicense userLicense);

    List<UserLicenseReadResponseDto> toUserLicenseReadAllResponseDto(List<UserLicense> userLicenses);

    UserLicenseCountResponseDto toUserLicenseCountResponseDto(
            Long userId, Long recipeCount, Long businessCount, Long patentCount);
}
