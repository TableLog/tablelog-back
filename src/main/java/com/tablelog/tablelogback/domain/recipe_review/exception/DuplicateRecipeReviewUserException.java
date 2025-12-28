package com.tablelog.tablelogback.domain.recipe_review.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class DuplicateRecipeReviewUserException extends CustomException {
    public DuplicateRecipeReviewUserException(final ErrorCode errorCode){
        super(errorCode);
    }
}
