package com.tablelog.tablelogback.domain.recipe_review.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessRecipeReviewException extends CustomException {
    public ForbiddenAccessRecipeReviewException(final ErrorCode errorCode){
        super(errorCode);
    }
}
