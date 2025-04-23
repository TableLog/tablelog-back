package com.tablelog.tablelogback.domain.recipe_food.entity;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.FoodUnit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE_FOOD")
@Entity
public class RecipeFood extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private FoodUnit recipeFoodUnit;

    @Column(nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private Long foodId;

    @Builder
    public RecipeFood(final Integer amount, final FoodUnit recipeFoodUnit, final Long recipeId, final Long foodId){
        this.amount = amount;
        this.recipeFoodUnit = recipeFoodUnit;
        this.recipeId = recipeId;
        this.foodId = foodId;
    }

    public void updateRecipeFood(Integer amount, FoodUnit recipeFoodUnit, Long recipeId, Long foodId){
        this.amount = amount;
        this.recipeFoodUnit = recipeFoodUnit;
        this.recipeId = recipeId;
        this.foodId = foodId;
    }
}
