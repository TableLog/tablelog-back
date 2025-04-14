package com.tablelog.tablelogback.domain.food.dto.service.response;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record FoodReadAllServiceResponseDto(
        Long id,
        String foodName,
        FoodUnit foodUnit,
        Integer cal
) {
}
