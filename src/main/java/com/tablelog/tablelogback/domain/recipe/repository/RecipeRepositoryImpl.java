package com.tablelog.tablelogback.domain.recipe.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeFilterConditionDto;
import com.tablelog.tablelogback.domain.recipe.entity.QRecipe;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@RequiredArgsConstructor
public class RecipeRepositoryImpl implements CustomRecipeRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Recipe> findAllByFilter(RecipeFilterConditionDto condition, PageRequest pageRequest) {
        QRecipe recipe = QRecipe.recipe;

        BooleanBuilder builder = new BooleanBuilder();

        // 조건에 따라 필터링
        if (condition.recipeCategory() != null && !condition.recipeCategory().isEmpty()) {
            builder.and(recipe.recipeCategoryList.any().in(condition.recipeCategory()));
        }

        if (condition.cookingTime() != null) {
            builder.and(recipe.cookingTime.eq(condition.cookingTime()));
        }

        if (condition.cal() != null) {
            builder.and(recipe.totalCal.loe(condition.cal()));
        }

        if (condition.recipePrice() != null) {
            builder.and(recipe.price.eq(condition.recipePrice()));
        }

        JPAQuery<Recipe> query = queryFactory
                .selectFrom(recipe)
                .where(builder)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize() + 1);

        List<Recipe> result = query.fetch();
        boolean hasNext = result.size() > pageRequest.getPageSize();

        if (hasNext) {
            result.remove(result.size() - 1);
        }

        return new SliceImpl<>(result, pageRequest, hasNext);
    }
}
