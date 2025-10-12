package com.tablelog.tablelogback.domain.recipe_review.dto.service;

import java.util.List;

public record RecipeReviewSliceResponseByUserDto(
        List<RecipeReviewReadResponseByUserDto> contents,
        boolean hasNext,
        Boolean isWriter
) {
}
