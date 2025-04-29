package com.tablelog.tablelogback.domain.recipe_process.dto.service;

import java.util.List;

public record RecipeProcessCreateRequestDto(
        List<RecipeProcessDto> dtos
){}