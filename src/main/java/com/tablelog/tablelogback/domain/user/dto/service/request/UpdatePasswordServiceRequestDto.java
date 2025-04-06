package com.tablelog.tablelogback.domain.user.dto.service.request;

public record UpdatePasswordServiceRequestDto(
        String newPassword,
        String confirmNewPassword
) {
}
