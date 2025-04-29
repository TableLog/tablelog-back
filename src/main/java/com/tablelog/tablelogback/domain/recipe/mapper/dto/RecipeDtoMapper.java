package com.tablelog.tablelogback.domain.recipe.mapper.dto;

import com.tablelog.tablelogback.domain.recipe.dto.controller.RecipeCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.controller.RecipeUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeUpdateServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RecipeDtoMapper {
    RecipeCreateServiceRequestDto toRecipeServiceRequestDto(
            RecipeCreateControllerRequestDto controllerRequestDto
    );

    RecipeUpdateServiceRequestDto toRecipeUpdateServiceDto(
            RecipeUpdateControllerRequestDto controllerRequestDto
    );
}
