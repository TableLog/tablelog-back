package com.tablelog.tablelogback.domain.user.dto.controller;

import com.tablelog.tablelogback.global.enums.UserProvider;

public record UserSignUpControllerRequestDto(
        String email,
        String password,
        String confirmPassword,
        String nickname,
        String userName,
        String birthday,
        UserProvider provider,
        String imgUrl,
        Boolean marketingOptIn
) {
}
