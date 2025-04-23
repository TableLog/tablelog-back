package com.tablelog.tablelogback.domain.recipe.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundRecipeException extends CustomException {
    public NotFoundRecipeException(final ErrorCode errorCode){
        super(errorCode);
    }
}
