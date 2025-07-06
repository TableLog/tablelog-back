package com.tablelog.tablelogback.domain.report.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {
    // 404
    NOT_FOUND_REPORT(HttpStatus.NOT_FOUND, "ERE404001");

    private final HttpStatus status;
    private final String message;
}
