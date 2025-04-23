package com.tablelog.tablelogback.domain.recipe_process.dto.service;

public record RecipeProcessUpdateServiceRequestDto(
        short sequence,
        String rpTitle,
        String description
) {
}
