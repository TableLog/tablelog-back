package com.tablelog.tablelogback.domain.recipe_memo.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeMemoErrorCode implements ErrorCode {
    // 400
    ALREADY_EXIST_RECIPE_MEMO(HttpStatus.BAD_REQUEST, "ERM400001"),

    // 404
    NOT_FOUND_RECIPE_MEMO(HttpStatus.NOT_FOUND, "ERM404001");

    private final HttpStatus status;
    private final String message;
}
