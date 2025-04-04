package com.tablelog.tablelogback.domain.user.mapper.dto;

import com.tablelog.tablelogback.domain.user.dto.controller.UserLoginControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserSignUpControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserLoginServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserDtoMapper {
    UserSignUpServiceRequestDto toUserSignUpServiceRequestDto(
            UserSignUpControllerRequestDto userSignUpControllerRequestDto
    );

    UserLoginServiceRequestDto toUserLoginServiceRequestDto(
            UserLoginControllerRequestDto userLoginControllerRequestDto
    );
}
