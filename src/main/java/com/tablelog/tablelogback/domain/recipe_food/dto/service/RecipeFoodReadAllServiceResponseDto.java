package com.tablelog.tablelogback.domain.recipe_food.dto.service;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record RecipeFoodReadAllServiceResponseDto(
        Long id,
        Double amount,
        FoodUnit recipeFoodUnit,
        Long foodId,
        String foodName
//        Double cal
) {
}
