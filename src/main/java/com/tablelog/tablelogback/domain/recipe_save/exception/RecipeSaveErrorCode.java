package com.tablelog.tablelogback.domain.recipe_save.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeSaveErrorCode implements ErrorCode {
    // 400
    ALREADY_EXIST_RECIPE_SAVE(HttpStatus.BAD_REQUEST, "ERS400001"),

    // 404
    NOT_FOUND_RECIPE_SAVE(HttpStatus.NOT_FOUND, "ERS404001");

    private final HttpStatus status;
    private final String message;
}
