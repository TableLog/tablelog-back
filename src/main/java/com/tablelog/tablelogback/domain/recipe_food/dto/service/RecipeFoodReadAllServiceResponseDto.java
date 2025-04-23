package com.tablelog.tablelogback.domain.recipe_food.dto.service;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record RecipeFoodReadAllServiceResponseDto(
        Integer amount,
        FoodUnit recipeFoodUnit,
        Long foodId
) {
}
