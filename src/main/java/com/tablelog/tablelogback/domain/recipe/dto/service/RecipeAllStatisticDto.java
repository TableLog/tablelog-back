package com.tablelog.tablelogback.domain.recipe.dto.service;

import java.util.List;

public record RecipeAllStatisticDto(
        Long totalCount,
        List<RecipeStatisticDto> dailyCounts
) {
}
