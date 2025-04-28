package com.tablelog.tablelogback.domain.recipe_food.dto.service;

import java.util.List;

public record RecipeFoodSliceResponseDto(
        List<RecipeFoodReadAllServiceResponseDto> recipeFoods,
        Boolean hasNext
) {
}
