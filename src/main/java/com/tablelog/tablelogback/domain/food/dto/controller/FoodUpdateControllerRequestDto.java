package com.tablelog.tablelogback.domain.food.dto.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record FoodUpdateControllerRequestDto(
        String foodName,
        FoodUnit foodUnit,
        Integer cal
) {
}
