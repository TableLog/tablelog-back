package com.tablelog.tablelogback.domain.recipe_process.dto.service.request;

public record RecipeProcessCreateServiceRequestDto(
        short sequence,
        String description
) {
}
