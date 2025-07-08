package com.tablelog.tablelogback.domain.user_license.dto.service;

import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.LicenseType;

import java.time.LocalDateTime;

public record UserLicenseReadResponseDto(
        Long id,
        Long userId,
        String licenseName,
        LicenseType licenseType,
        ApplyStatus status,
        LocalDateTime modifiedAt
) {
}
