package com.tablelog.tablelogback.domain.recipe.repository;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeFilterConditionDto;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomRecipeRepository {
    Slice<Recipe> findAllByFilter(RecipeFilterConditionDto condition, Pageable pageable);
}
