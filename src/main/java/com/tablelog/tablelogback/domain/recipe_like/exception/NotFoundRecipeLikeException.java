package com.tablelog.tablelogback.domain.recipe_like.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundRecipeLikeException extends CustomException {
    public NotFoundRecipeLikeException(final ErrorCode errorCode){
        super(errorCode);
    }
}
