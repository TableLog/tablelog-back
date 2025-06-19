package com.tablelog.tablelogback.domain.shopping_list.dto.service;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record ShoppingListReadAllServiceResponseDto(
        Long id,
        String foodName,
        FoodUnit foodUnit,
        Integer amount,
        Long foodId,
        Boolean isChecked,
        Long userId
) {
}
