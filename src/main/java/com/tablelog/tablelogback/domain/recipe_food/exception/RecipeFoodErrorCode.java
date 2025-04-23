package com.tablelog.tablelogback.domain.recipe_food.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeFoodErrorCode implements ErrorCode {
    // 403
    FORBIDDEN_ACCESS_RECIPE_FOOD(HttpStatus.FORBIDDEN, "ERF403001"),
    INVALID_RECIPE_FOOD(HttpStatus.BAD_REQUEST, "ERF403002"),

    // 404
    NOT_FOUND_RECIPE_FOOD(HttpStatus.NOT_FOUND, "ERF404001");

    private final HttpStatus status;
    private final String message;
}
