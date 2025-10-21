package com.tablelog.tablelogback.domain.recipe.mapper.entity;

import com.tablelog.tablelogback.domain.recipe.dto.service.*;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipeEntityMapper {
    @Mapping(source = "user.id", target = "userId")
    Recipe toRecipe(RecipeCreateServiceRequestDto requestDto, String folderName,
                    String imageUrl, User user, Integer recipePoint);

    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "isSaved", target = "isSaved")
    @Mapping(source = "nickname", target = "user")
    @Mapping(source = "isExpertWriter", target = "isExpertWriter")
    @Mapping(source = "recipe.userId", target = "writerId")
    RecipeReadResponseDto toRecipeReadDetailResponseDto(
            Recipe recipe, Long likeCount, Boolean isSaved, String nickname,
            Boolean isExpertWriter, Boolean isWriter, Boolean hasPurchased);

    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "isSaved", target = "isSaved")
    @Mapping(source = "nickname", target = "user")
    RecipeReadAllServiceResponseDto toRecipeReadResponseDto(
            Recipe recipe, Long likeCount, Boolean isSaved, String nickname, Boolean isWriter);

    @Mapping(source = "userName",target = "userName")
    @Mapping(source = "nickname",target = "nickname")
    RecipeReadByAdminResponseDto toRecipeReadByAdminResponseDto(Recipe recipe, String userName, String nickname);
}
