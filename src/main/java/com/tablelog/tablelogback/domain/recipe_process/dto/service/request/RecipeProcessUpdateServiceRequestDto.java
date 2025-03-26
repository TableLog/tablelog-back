package com.tablelog.tablelogback.domain.recipe_process.dto.service.request;

public record RecipeProcessUpdateServiceRequestDto(
        short sequence,
        String description,
        Boolean imageChange
) {
}
