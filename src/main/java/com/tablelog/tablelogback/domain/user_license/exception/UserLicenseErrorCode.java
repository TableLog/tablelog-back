package com.tablelog.tablelogback.domain.user_license.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserLicenseErrorCode implements ErrorCode {
    // 404
    NOT_FOUND_USER_LICENSE(HttpStatus.NOT_FOUND, "EUL404001");

    private final HttpStatus status;
    private final String message;
}
