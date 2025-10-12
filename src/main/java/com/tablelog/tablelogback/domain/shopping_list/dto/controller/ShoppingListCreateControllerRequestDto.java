package com.tablelog.tablelogback.domain.shopping_list.dto.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record ShoppingListCreateControllerRequestDto(
        FoodUnit foodUnit,
        Integer amount,
        Long foodId
) {
}
