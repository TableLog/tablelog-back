package com.tablelog.tablelogback.domain.recipe.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeFilterConditionDto;
import com.tablelog.tablelogback.domain.recipe.entity.QRecipe;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.global.enums.RecipeCalorieRange;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@RequiredArgsConstructor
public class RecipeRepositoryImpl implements CustomRecipeRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Recipe> findAllByFilter(RecipeFilterConditionDto condition, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;

        BooleanBuilder builder = new BooleanBuilder();

        // 조건에 따라 필터링
        if (condition.recipeCategory() != null && !condition.recipeCategory().isEmpty()) {
            builder.and(recipe.recipeCategoryList.any().in(condition.recipeCategory()));
        }

        if (condition.cookingTime() != null) {
            builder.and(recipe.cookingTime.eq(condition.cookingTime()));
        }

        if (condition.calorieRange() != null) {
            RecipeCalorieRange r = condition.calorieRange();
            if (r.getMin() != null) {
                builder.and(recipe.totalCal.goe(r.getMin()));
            }
            if (r.getMax() != null) {
                builder.and(recipe.totalCal.loe(r.getMax()));
            }
        }

        if (condition.recipePrice() != null) {
            builder.and(recipe.price.eq(condition.recipePrice()));
        }

        JPAQuery<Recipe> query = queryFactory
                .selectFrom(recipe)
                .where(builder)
                .orderBy(recipe.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1);

        List<Recipe> result = query.fetch();
        boolean hasNext = result.size() > pageable.getPageSize();

        if (hasNext) {
            result.remove(result.size() - 1);
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }
}
