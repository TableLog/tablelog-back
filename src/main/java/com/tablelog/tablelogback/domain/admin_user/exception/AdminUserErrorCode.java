package com.tablelog.tablelogback.domain.admin_user.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminUserErrorCode implements ErrorCode {
    // 404
    NOT_FOUND_ADMIN_USER(HttpStatus.NOT_FOUND, "EAU404001");

    private final HttpStatus status;
    private final String message;
}
