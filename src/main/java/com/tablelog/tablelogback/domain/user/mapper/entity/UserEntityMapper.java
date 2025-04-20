package com.tablelog.tablelogback.domain.user.mapper.entity;

import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PasswordTranslator.class)
public interface UserEntityMapper {
    @Mapping(source = "serviceRequestDto.password", target = "password", qualifiedBy = EncoderPassword.class)
    @Mapping(source = "userRole", target = "userRole")
    @Mapping(source = "serviceRequestDto.provider", target = "provider")
    @Mapping(source = "profileImgUrl", target = "profileImgUrl")
    @Mapping(source = "folderName", target = "folderName")
    User toUser(UserSignUpServiceRequestDto serviceRequestDto, UserRole userRole,
                String profileImgUrl, String folderName);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "userRole", target = "userRole")
    @Mapping(source = "serviceRequestDto.provider", target = "provider")
    @Mapping(source = "profileImgUrl", target = "profileImgUrl")
    @Mapping(source = "folderName", target = "folderName")
    User toSocialUser(UserSignUpServiceRequestDto serviceRequestDto, String password,
                      UserRole userRole, String profileImgUrl, String folderName);

    UserLoginResponseDto toUserLoginResponseDto(User user);
}
