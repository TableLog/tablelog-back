package com.tablelog.tablelogback.domain.recipe_process.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeProcessErrorCode implements ErrorCode {
    // 403
    FORBIDDEN_ACCESS_RECIPE_PROCESS(HttpStatus.FORBIDDEN, "ERP403001"),
    INVALID_RECIPE_PROCESS(HttpStatus.BAD_REQUEST, "ERP403002"),

    // 404
    NOT_FOUND_RECIPE_PROCESS(HttpStatus.NOT_FOUND, "ERO404001");

    private final HttpStatus status;
    private final String message;
}
