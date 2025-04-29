package com.tablelog.tablelogback.domain.recipe.dto.service;

import java.util.List;

public record RecipeSliceResponseDto(
        List<RecipeReadAllServiceResponseDto> contents,
        boolean hasNext
) {
}
