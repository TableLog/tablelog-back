package com.tablelog.tablelogback.domain.recipe.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class InsufficientPointBalanceException extends CustomException {
    public InsufficientPointBalanceException(final ErrorCode errorCode){
        super(errorCode);
    }
}
