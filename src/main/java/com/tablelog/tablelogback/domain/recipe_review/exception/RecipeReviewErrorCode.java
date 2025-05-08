package com.tablelog.tablelogback.domain.recipe_review.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeReviewErrorCode implements ErrorCode {
    // 403
    FORBIDDEN_ACCESS_RECIPE_REVIEW(HttpStatus.FORBIDDEN, "ERR403001"),

    // 404
    NOT_FOUND_RECIPE_REVIEW(HttpStatus.NOT_FOUND, "ERR404001");

    private final HttpStatus status;
    private final String message;
}
