package com.tablelog.tablelogback.domain.recipe.dto.service;

import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;

import java.util.List;

public record RecipeFoodPreviewDto(
        String title,
        String imageUrl,
        List<RecipeFoodReadAllServiceResponseDto> recipeFoods
) {
}
