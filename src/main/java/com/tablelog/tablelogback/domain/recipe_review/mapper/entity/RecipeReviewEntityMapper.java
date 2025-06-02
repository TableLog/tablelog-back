package com.tablelog.tablelogback.domain.recipe_review.mapper.entity;

import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewReadResponseDto;
import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipeReviewEntityMapper {
    @Mapping(source = "user.nickname", target = "user")
    @Mapping(source = "recipeId", target = "recipeId")
    RecipeReview toRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto, Long recipeId, User user);
    RecipeReviewReadResponseDto toRecipeReviewReadResponseDto(RecipeReview recipeReview, Boolean isReviewer);

    @Mapping(source = "modifiedAt", target = "modifiedAt")
    List<RecipeReviewReadResponseDto> toRecipeReviewReadAllResponseDtoLists(List<RecipeReview> recipeReviewList);
}
