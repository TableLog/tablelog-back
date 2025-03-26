package com.tablelog.tablelogback.domain.recipe_process.mapper.dto;

import com.tablelog.tablelogback.domain.recipe_process.dto.controller.RecipeProcessCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.request.RecipeProcessCreateServiceRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RecipeProcessDtoMapper {
    RecipeProcessCreateServiceRequestDto toRecipeProcessServiceRequestDto(
            RecipeProcessCreateControllerRequestDto controllerRequestDto);
}
