package com.tablelog.tablelogback.domain.user.dto.service.response;

import com.tablelog.tablelogback.global.enums.UserRole;

public record UserLoginDto(
        UserRole userRole
) {
}
