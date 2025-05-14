package com.tablelog.tablelogback.domain.recipe_review.dto.service;

import java.util.List;

public record RecipeReviewSliceResponseDto(
        List<RecipeReviewReadResponseDto> contents,
        boolean hasNext
) {
}
