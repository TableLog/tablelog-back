package com.tablelog.tablelogback.domain.user_license.dto.service;

import java.util.List;

public record UserLicenseSliceResponseDto(
        List<UserLicenseReadResponseDto> contents,
        Boolean hasNext
) {
}
