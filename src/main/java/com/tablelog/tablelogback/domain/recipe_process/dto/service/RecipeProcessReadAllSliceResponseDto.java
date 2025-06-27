package com.tablelog.tablelogback.domain.recipe_process.dto.service;

import java.util.List;

    public record RecipeProcessReadAllSliceResponseDto(
            List<RecipeProcessSliceResponseDto> recipeProcesses,
            Boolean hasNext,
            int totalCount
    ) {
    }
