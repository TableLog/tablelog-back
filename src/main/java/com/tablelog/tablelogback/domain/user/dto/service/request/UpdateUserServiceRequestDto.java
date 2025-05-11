package com.tablelog.tablelogback.domain.user.dto.service.request;

public record UpdateUserServiceRequestDto(
        String email,
        String password,
        String nickname,
        String profileImgUrl,
        Boolean marketingOptIn
) {
}
