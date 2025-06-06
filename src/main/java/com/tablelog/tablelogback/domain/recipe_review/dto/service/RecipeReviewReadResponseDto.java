package com.tablelog.tablelogback.domain.recipe_review.dto.service;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RecipeReviewReadResponseDto(
        Long id,
        String content,
        @Schema(description = "Start value (Byte)", example = "1", type = "integer", format = "int32")
        Byte star,
        Long recipeId,
        String user,
        LocalDateTime modifiedAt,
        Long prrId,
        boolean isReviewer
) {
}
