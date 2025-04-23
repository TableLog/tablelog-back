package com.tablelog.tablelogback.domain.recipe_food.mapper.dto;

import com.tablelog.tablelogback.domain.recipe_food.dto.controller.RecipeFoodCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.controller.RecipeFoodUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodUpdateServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RecipeFoodDtoMapper {
    RecipeFoodCreateServiceRequestDto toRecipeFoodServiceRequestDto(
            RecipeFoodCreateControllerRequestDto controllerRequestDto
    );
}
