package com.tablelog.tablelogback.domain.recipe.dto.service;

import com.tablelog.tablelogback.global.enums.RecipeCategory;

import java.util.List;

public record RecipeReadByAdminResponseDto(
        Long id,
        String title,
        String userName,
        String nickname,
        String createdAt,
        String intro,
        List<RecipeCategory> recipeCategoryList
) {
}
