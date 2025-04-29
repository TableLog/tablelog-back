package com.tablelog.tablelogback.domain.food.dto.service.response;

import java.util.List;

public record FoodSliceResponseDto(
        List<FoodReadAllServiceResponseDto> foods,
        Boolean hasNext
) {
}
