package com.tablelog.tablelogback.domain.user.dto.service.response;

import com.tablelog.tablelogback.global.enums.UserProvider;

public record OAuthAccountResponseDto(
        UserProvider provider,
        String email
) {
}
