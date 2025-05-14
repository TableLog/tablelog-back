package com.tablelog.tablelogback.domain.recipe_review.mapper.dto;

import com.tablelog.tablelogback.domain.recipe_review.dto.controller.RecipeReviewCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.controller.RecipeReviewUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewUpdateServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RecipeReviewDtoMapper {
    RecipeReviewCreateServiceRequestDto toRecipeReviewServiceRequestDto(
            RecipeReviewCreateControllerRequestDto controllerRequestDto);

    RecipeReviewUpdateServiceRequestDto toRecipeReviewUpdateServiceRequestDto(
            RecipeReviewUpdateControllerRequestDto controllerRequestDto
    );
}
