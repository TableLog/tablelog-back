package com.tablelog.tablelogback.domain.food.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FoodErrorCode implements ErrorCode {
    // 403
    ALREADY_EXISTS_FOOD_NAME(HttpStatus.BAD_REQUEST, "EF403001"),
    FORBIDDEN_CREATE_FOOD(HttpStatus.FORBIDDEN, "EF403002"),
    FORBIDDEN_UPDATE_FOOD(HttpStatus.FORBIDDEN, "EF403003"),
    FORBIDDEN_DELETE_FOOD(HttpStatus.FORBIDDEN, "EF403004"),

    // 404
    NOT_FOUND_FOOD(HttpStatus.NOT_FOUND, "EF404001");

    private final HttpStatus status;
    private final String message;
}
