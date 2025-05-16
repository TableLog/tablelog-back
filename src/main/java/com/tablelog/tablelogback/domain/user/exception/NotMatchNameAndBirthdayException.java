package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotMatchNameAndBirthdayException extends CustomException {
    public NotMatchNameAndBirthdayException(final ErrorCode errorCode){
        super(errorCode);
    }
}
