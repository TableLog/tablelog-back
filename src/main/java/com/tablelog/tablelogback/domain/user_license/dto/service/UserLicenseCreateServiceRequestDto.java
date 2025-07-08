package com.tablelog.tablelogback.domain.user_license.dto.service;

import com.tablelog.tablelogback.global.enums.LicenseType;

public record UserLicenseCreateServiceRequestDto(
        String licenseName,
        LicenseType licenseType
) {
}
