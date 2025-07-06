package com.tablelog.tablelogback.domain.report.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundReportException extends CustomException {
    public NotFoundReportException(final ErrorCode errorCode){
        super(errorCode);
    }
}
