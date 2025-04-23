package com.tablelog.tablelogback.domain.recipe_process.dto.controller;

public record RecipeProcessUpdateControllerRequestDto(
        short sequence,
        String rpTitle,
        String description
) {
}
