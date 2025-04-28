package com.tablelog.tablelogback.domain.recipe.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeErrorCode implements ErrorCode{
    // 403
    FORBIDDEN_ACCESS_RECIPE(HttpStatus.FORBIDDEN, "ER403001"),

    // 404
    NOT_FOUND_RECIPE(HttpStatus.NOT_FOUND, "식재료를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
