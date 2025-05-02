package com.tablelog.tablelogback.domain.user.dto.service.request;

public record UpdatePasswordServiceRequestDto(
        String email,
        String newPassword
) {
}
