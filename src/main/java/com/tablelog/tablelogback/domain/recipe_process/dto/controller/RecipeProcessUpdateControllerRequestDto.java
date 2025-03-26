package com.tablelog.tablelogback.domain.recipe_process.dto.controller;

public record RecipeProcessUpdateControllerRequestDto(
        short sequence,
        String description,
        Boolean imageChange
) {
}
