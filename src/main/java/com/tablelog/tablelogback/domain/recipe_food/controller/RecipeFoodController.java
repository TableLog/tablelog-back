package com.tablelog.tablelogback.domain.recipe_food.controller;

import com.tablelog.tablelogback.global.enums.FoodAmount;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> readRecipeFood(
            @PathVariable Long recipeFoodId
    ){
        Map<String, Object> recipe_food1 = new HashMap<>();
        recipe_food1.put("foodAmount", "ts");
        recipe_food1.put("foodName", "사과");
        recipe_food1.put("user", "user1");
        return recipe_food1;
    }

    @GetMapping("/recipes/{recipeId}/recipe-food")
    public List<Map<String, Object>> readAllRecipeFoodsByRecipeId(
            @PathVariable Long recipeId
    ){
        List<Map<String, Object>> recipe_foods = new ArrayList<>();
        Map<String, Object> recipe_food1 = new HashMap<>();
        recipe_food1.put("foodAmount", "ts");
        recipe_food1.put("foodName", "사과");
        recipe_food1.put("user", "user1");
        Map<String, Object> recipe_food2 = new HashMap<>();
        recipe_food2.put("foodAmount", "ts");
        recipe_food2.put("foodName", "사과");
        recipe_food2.put("user", "user1");
        recipe_foods.add(recipe_food1);;
        recipe_foods.add(recipe_food2);
        return recipe_foods;
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
