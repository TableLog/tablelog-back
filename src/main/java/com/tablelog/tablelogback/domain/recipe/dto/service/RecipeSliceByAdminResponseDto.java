package com.tablelog.tablelogback.domain.recipe.dto.service;

import java.util.List;

public record RecipeSliceByAdminResponseDto(
        List<RecipeReadByAdminResponseDto> contents,
        boolean hasNext
) {
}
