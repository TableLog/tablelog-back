package com.tablelog.tablelogback.domain.food.dto.service.request;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record FoodCreateServiceRequestDto(
        String foodName,
        FoodUnit foodUnit,
        Integer cal
) {
}
