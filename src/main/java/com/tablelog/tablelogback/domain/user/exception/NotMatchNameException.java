package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotMatchNameException extends CustomException {
    public NotMatchNameException(final ErrorCode errorCode){
        super(errorCode);
    }
}
