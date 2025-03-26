package com.tablelog.tablelogback.domain.recipe_food.controller;

import com.tablelog.tablelogback.global.enums.FoodAmount;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 식재료 API", description = "")
public class RecipeFoodController {

    @PostMapping("/recipe-food/{recipeId}")
    public void createRecipeFood(
            @PathVariable Long recipeId,
            FoodAmount foodAmount,
            String foodName
    ) throws IOException {

    }

    @GetMapping("/recipe-food/{recipeFoodId}")
    public void readFood(
            @PathVariable Long recipeFoodId
    ){

    }

    @GetMapping("/recipes/{recipeId}/recipe-food")
    public void readAllRecipeFoodsByRecipeId(
            @PathVariable Long recipeId
    ){

    }

    @PutMapping("/recipe-food/{recipeFoodId}")
    public void updateRecipeFood(
            @PathVariable Long recipeFoodId,
            FoodAmount foodAmount,
            String foodName
    ) throws IOException {

    }

    @DeleteMapping("/recipe-food/{recipeFoodId}")
    public void deleteRecipeFood(
            @PathVariable Long recipeFoodId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

    }
}
