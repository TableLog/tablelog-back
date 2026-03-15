package com.tablelog.tablelogback.domain.recipe_food.dto.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record RecipeFoodUpdateControllerRequestDto(
        Double amount,
        FoodUnit recipeFoodUnit
) {
}
