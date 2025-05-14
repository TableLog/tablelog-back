package com.tablelog.tablelogback.domain.recipe_review.dto.controller;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecipeReviewCreateControllerRequestDto(
        String content,
        @Schema(description = "Start value (Byte)", example = "1", type = "integer", format = "int32")
        Byte star,
        Long prrId
) {
}
