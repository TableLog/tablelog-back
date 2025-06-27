package com.tablelog.tablelogback.domain.recipe_process.dto.service;

import java.util.List;

public record RecipeProcessSliceResponseDto(
        RecipeProcessReadAllServiceResponseDto recipeProcesses,
        Boolean hasPrev,
        Boolean hasNext
) {
}
