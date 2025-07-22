package com.tablelog.tablelogback.domain.inquiry.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InquiryErrorCode implements ErrorCode {
    // 400
    INVALID_INQUIRY_TYPE(HttpStatus.BAD_REQUEST, "EI400001"),

    // 404
    NOT_FOUND_INQUIRY(HttpStatus.NOT_FOUND, "EI404001");

    private final HttpStatus status;
    private final String message;
}
