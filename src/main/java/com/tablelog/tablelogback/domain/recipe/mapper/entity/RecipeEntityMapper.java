package com.tablelog.tablelogback.domain.recipe.mapper.entity;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeFoodPreviewDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipeEntityMapper {
    @Mapping(source = "user.id", target = "userId")
    Recipe toRecipe(RecipeCreateServiceRequestDto requestDto, String folderName, String imgUrl, User user);
//    @Mapping(source = "likeCount", target = "likeCount")
////    @Mapping(source = "isSaved", target = "isSaved")
//    RecipeReadAllServiceResponseDto toRecipeReadResponseDto(Recipe recipe, Long likeCount);
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "isSaved", target = "isSaved")
    RecipeReadAllServiceResponseDto toRecipeReadResponseDto(Recipe recipe, Long likeCount, Boolean isSaved);
    List<RecipeReadAllServiceResponseDto> toRecipeReadAllResponseDto(List<Recipe> recipes);
    RecipeFoodPreviewDto toRecipeFoodPreviewReadResponseDto(Recipe recipe, List<RecipeFood> recipeFoods);
}
