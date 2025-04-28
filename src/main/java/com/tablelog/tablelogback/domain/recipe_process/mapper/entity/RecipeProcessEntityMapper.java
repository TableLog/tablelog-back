package com.tablelog.tablelogback.domain.recipe_process.mapper.entity;

import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipeProcessEntityMapper {
    @Mapping(source = "recipeProcessImageUrls", target = "recipeProcessImageUrls")
    RecipeProcess toRecipeProcess(Long recipeId, RecipeProcessCreateServiceRequestDto serviceRequestDto,
                                  List<String> recipeProcessImageUrls);

    @Mapping(source = "recipeProcessImageUrls", target = "recipeProcessImageUrls")
    RecipeProcess toRecipeProcess(Long recipeId, RecipeProcessDto serviceRequestDto,
                                  List<String> recipeProcessImageUrls);

    RecipeProcessReadAllServiceResponseDto toRecipeProcessReadResponseDto(RecipeProcess recipeProcess);

    List<RecipeProcessReadAllServiceResponseDto> toRecipeProcessReadAllResponseDto(List<RecipeProcess> recipeProcesses);
}
