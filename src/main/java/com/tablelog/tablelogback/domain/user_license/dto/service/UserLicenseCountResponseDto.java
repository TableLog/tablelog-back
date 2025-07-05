package com.tablelog.tablelogback.domain.user_license.dto.service;

public record UserLicenseCountResponseDto(
        Long userId,
        Long recipeCount,
        Long businessCount,
        Long patentCount
) {
}
