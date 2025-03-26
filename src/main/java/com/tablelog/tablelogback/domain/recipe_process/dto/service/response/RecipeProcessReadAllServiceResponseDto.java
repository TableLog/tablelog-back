package com.tablelog.tablelogback.domain.recipe_process.dto.service.response;

public record RecipeProcessReadAllServiceResponseDto(
        Long id,
        short sequence,
        String description,
        String imgUrl
) {
}
