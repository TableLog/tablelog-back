package com.tablelog.tablelogback.domain.recipe_process.mapper.entity;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RecipeProcessEntityMapper {
//    @Mapping(source = "user",target = "user")
    @Mapping(source = "recipe", target = "recipe")
    RecipeProcess toRecipeProcess(RecipeProcessCreateServiceRequestDto requestDto,
                                  String imgUrl,
//                                  User user,
                                  Recipe recipe
    );
}
