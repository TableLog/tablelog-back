package com.tablelog.tablelogback.domain.recipe_like.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeLikeErrorCode implements ErrorCode {
    // 400
    ALREADY_EXIST_RECIPE_LIKE(HttpStatus.BAD_REQUEST, "ERL400001"),

    // 404
    NOT_FOUND_RECIPE_LIKE(HttpStatus.NOT_FOUND, "ERL404001");

    private final HttpStatus status;
    private final String message;
}
