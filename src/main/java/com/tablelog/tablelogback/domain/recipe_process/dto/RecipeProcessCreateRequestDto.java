package com.tablelog.tablelogback.domain.recipe_process.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecipeProcessCreateRequestDto {
    private List<RecipeProcessDto> dtos;
}
