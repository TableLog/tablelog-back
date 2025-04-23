package com.tablelog.tablelogback.domain.user.dto.service.request;

import com.tablelog.tablelogback.global.enums.UserProvider;

public record UserSignUpServiceRequestDto(
        String email,
        String password,
        String confirmPassword,
        String nickname,
        String name,
        String birthday,
        UserProvider provider,
        String imgUrl
) {
}
