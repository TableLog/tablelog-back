package com.tablelog.tablelogback.domain.user.dto.service.request;

public record UpdateUserServiceRequestDto(
        String newEmail,
        String newPassword,
        String confirmNewPassword,
        String nickname,
        String profileImgUrl,
        Boolean ImageChange
) {
}
