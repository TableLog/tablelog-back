package com.tablelog.tablelogback.domain.shopping_list.dto.service;

import java.util.List;

public record ShoppingListSliceResponseDto(
        Long listCount,
        List<ShoppingListReadAllServiceResponseDto> shoppingLists,
        Boolean hasNext
) {
}
