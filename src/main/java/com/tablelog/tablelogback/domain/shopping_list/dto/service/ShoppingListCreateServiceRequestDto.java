package com.tablelog.tablelogback.domain.shopping_list.dto.service;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record ShoppingListCreateServiceRequestDto(
        FoodUnit foodUnit,
        Integer amount,
        Long foodId
) {
}
