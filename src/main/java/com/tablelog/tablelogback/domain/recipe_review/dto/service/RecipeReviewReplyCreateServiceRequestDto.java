package com.tablelog.tablelogback.domain.recipe_review.dto.service;

public record RecipeReviewReplyCreateServiceRequestDto(
        String content,
        Long prrId
) {
}
