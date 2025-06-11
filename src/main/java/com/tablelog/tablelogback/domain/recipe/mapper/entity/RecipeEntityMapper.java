package com.tablelog.tablelogback.domain.recipe.mapper.entity;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeFoodPreviewDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeReadResponseDto;
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
    Recipe toRecipe(RecipeCreateServiceRequestDto requestDto, String folderName, String imageUrl, User user, Integer recipePoint);
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "isSaved", target = "isSaved")
    @Mapping(source = "nickname", target = "user")
    RecipeReadResponseDto toRecipeReadDetailResponseDto(
            Recipe recipe, Long likeCount, Boolean isSaved, String nickname, Boolean isWriter, Boolean hasPurchased);
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "isSaved", target = "isSaved")
    @Mapping(source = "nickname", target = "user")
    RecipeReadAllServiceResponseDto toRecipeReadResponseDto(
            Recipe recipe, Long likeCount, Boolean isSaved, String nickname, Boolean isWriter);
    List<RecipeReadAllServiceResponseDto> toRecipeReadAllResponseDto(List<Recipe> recipes);
    RecipeFoodPreviewDto toRecipeFoodPreviewReadResponseDto(Recipe recipe, List<RecipeFood> recipeFoods);
}
