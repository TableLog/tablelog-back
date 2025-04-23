package com.tablelog.tablelogback.domain.recipe_process.dto.service;

public record RecipeProcessCreateServiceRequestDto(
        short sequence,
        String rpTitle,
        String description
) {
}
