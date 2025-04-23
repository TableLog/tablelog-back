package com.tablelog.tablelogback.domain.recipe_food.dto.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record RecipeFoodUpdateControllerRequestDto(
        Integer amount,
        FoodUnit recipeFoodUnit,
        Long foodId
) {
}
