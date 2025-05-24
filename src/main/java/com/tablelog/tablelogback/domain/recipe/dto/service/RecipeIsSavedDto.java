package com.tablelog.tablelogback.domain.recipe.dto.service;

public record RecipeIsSavedDto(
        Long recipeId,
        Boolean isSaved
) {
    public RecipeIsSavedDto(Long recipeId) {
        this(recipeId, false);
    }
}
