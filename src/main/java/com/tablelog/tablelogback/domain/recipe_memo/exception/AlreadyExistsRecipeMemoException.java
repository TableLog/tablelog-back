package com.tablelog.tablelogback.domain.recipe_memo.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsRecipeMemoException extends CustomException {
    public AlreadyExistsRecipeMemoException(final ErrorCode errorCode){
        super(errorCode);
    }
}
