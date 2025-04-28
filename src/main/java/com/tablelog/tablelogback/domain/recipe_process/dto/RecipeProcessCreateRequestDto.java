package com.tablelog.tablelogback.domain.recipe_process.dto;

import java.util.List;

public record RecipeProcessCreateRequestDto(
        List<RecipeProcessDto> dtos
){}