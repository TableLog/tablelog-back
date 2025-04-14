package com.tablelog.tablelogback.domain.food.dto.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record FoodCreateControllerRequestDto(
        String foodName,
        FoodUnit foodUnit,
        Integer cal
) {
}
