package com.tablelog.tablelogback.domain.recipe_process.dto.service;

public record RecipeProcessSliceResponseDto(
        RecipeProcessReadAllServiceResponseDto recipeProcesses,
        Boolean hasPrev,
        Boolean hasNext
) {
}
