package com.tablelog.tablelogback.domain.recipe_like.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsRecipeLikeException extends CustomException {
    public AlreadyExistsRecipeLikeException(final ErrorCode errorCode){
        super(errorCode);
    }
}
