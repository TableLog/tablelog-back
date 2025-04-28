package com.tablelog.tablelogback.domain.recipe.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessRecipeException extends CustomException {
    public ForbiddenAccessRecipeException(final ErrorCode errorCode){
        super(errorCode);
    }
}
