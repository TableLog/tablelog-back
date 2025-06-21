package com.tablelog.tablelogback.domain.recipe.dto.service;

import java.util.List;

public record RecipeFoodPreviewSliceResponseDto(
        String title,
        String imageUrl,
        List<RecipeFoodPreviewDto> recipeFoods,
        Boolean hasNext
) {
}
