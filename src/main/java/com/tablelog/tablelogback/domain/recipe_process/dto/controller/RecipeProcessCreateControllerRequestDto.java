package com.tablelog.tablelogback.domain.recipe_process.dto.controller;

public record RecipeProcessCreateControllerRequestDto(
        short sequence,
        String rpTitle,
        String description
) {
}
