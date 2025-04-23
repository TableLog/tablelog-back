package com.tablelog.tablelogback.domain.recipe_process.mapper.dto;

import com.tablelog.tablelogback.domain.recipe_process.dto.controller.RecipeProcessCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.controller.RecipeProcessUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessUpdateServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RecipeProcessDtoMapper {
    RecipeProcessCreateServiceRequestDto toRecipeProcessServiceRequestDto(
            RecipeProcessCreateControllerRequestDto controllerRequestDto);

    RecipeProcessUpdateServiceRequestDto toRecipeProcessUpdateServiceDto(
            RecipeProcessUpdateControllerRequestDto controllerRequestDto
    );
}
