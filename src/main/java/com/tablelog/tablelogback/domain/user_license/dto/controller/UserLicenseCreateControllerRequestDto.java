package com.tablelog.tablelogback.domain.user_license.dto.controller;

import com.tablelog.tablelogback.global.enums.LicenseType;

public record UserLicenseCreateControllerRequestDto(
        String licenseName,
        LicenseType licenseType
) {
}
