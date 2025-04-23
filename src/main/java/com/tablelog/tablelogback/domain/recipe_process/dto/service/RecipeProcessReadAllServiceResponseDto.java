package com.tablelog.tablelogback.domain.recipe_process.dto.service;

import java.util.List;

public record RecipeProcessReadAllServiceResponseDto(
        short sequence,
        String rpTitle,
        String description,
        List<String> recipeProcessImageUrls
) {
}
