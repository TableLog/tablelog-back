package com.tablelog.tablelogback.domain.shopping_list.mapper.dto;

import com.tablelog.tablelogback.domain.shopping_list.dto.controller.ShoppingListCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.controller.ShoppingListUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListUpdateServiceRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ShoppingListDtoMapper {
    ShoppingListCreateServiceRequestDto toShoppingListCreateServiceDto(
            ShoppingListCreateControllerRequestDto controllerRequestDto
    );

    ShoppingListUpdateServiceRequestDto toShoppingListUpdateServiceDto(
            ShoppingListUpdateControllerRequestDto controllerRequestDto
    );
}
