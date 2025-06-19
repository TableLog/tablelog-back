package com.tablelog.tablelogback.domain.shopping_list.mapper.entity;

import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.entity.ShoppingList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ShoppingListEntityMapper {
    @Mapping(source = "userId",target = "userId")
    ShoppingList toShoppingList(ShoppingListCreateServiceRequestDto requestDto, Long userId);

    ShoppingListReadAllServiceResponseDto toShoppingListReadResponseDto(ShoppingList shoppingList);

    List<ShoppingListReadAllServiceResponseDto> toShoppingListReadAllResponseDto(List<ShoppingList> shoppingLists);
}
