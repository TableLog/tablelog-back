package com.tablelog.tablelogback.domain.recipe_review.dto.controller;

public record RecipeReviewCreateControllerRequestDto(
        String content,
        Float star
) {
}
