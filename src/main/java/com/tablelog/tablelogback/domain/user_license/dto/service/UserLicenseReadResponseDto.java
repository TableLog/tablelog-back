package com.tablelog.tablelogback.domain.user_license.dto.service;

import com.tablelog.tablelogback.global.enums.LicenseType;
import com.tablelog.tablelogback.global.enums.RequestStatus;

import java.time.LocalDateTime;

public record UserLicenseReadResponseDto(
        Long id,
        Long userId,
        String licenseName,
        LicenseType licenseType,
        RequestStatus status,
        LocalDateTime modifiedAt
) {
}
