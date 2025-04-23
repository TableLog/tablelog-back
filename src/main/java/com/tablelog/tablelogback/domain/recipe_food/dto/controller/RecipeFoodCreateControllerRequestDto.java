package com.tablelog.tablelogback.domain.recipe_food.dto.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record RecipeFoodCreateControllerRequestDto(
        Integer amount,
        FoodUnit recipeFoodUnit,
        Long foodId
) {
}
