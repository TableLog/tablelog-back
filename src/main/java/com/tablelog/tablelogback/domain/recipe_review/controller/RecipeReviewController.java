package com.tablelog.tablelogback.domain.recipe_review.controller;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "레시피 리뷰 API", description = "")
public class RecipeReviewController {
    @PostMapping("/recipe-reviews/{recipeId}")
    public void createRecipeReviews(
            @PathVariable Long recipeId,
            String content,
            @Schema(description = "Start value (Byte)", example = "1", type = "integer", format = "int32")
            Byte star
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {

    }

    @GetMapping("/recipe-reviews/{recipeReviewId}")
    public Map<String, Object> readRecipeReview(
            @PathVariable Long recipeReviewId
    ){
        Map<String, Object> recipe_review1 = new HashMap<>();
        recipe_review1.put("content", "가가가");
        recipe_review1.put("star", 2);
        recipe_review1.put("user", "user1");
        return recipe_review1;
    }

    @GetMapping("/recipes/{recipeId}/recipe-reviews")
    public List<Map<String, Object>> readAllRecipeReviewsByRecipeId(
            @PathVariable Long recipeId
    ){
        List<Map<String, Object>> recipe_reviews = new ArrayList<>();
        Map<String, Object> recipe_review1 = new HashMap<>();
        recipe_review1.put("content", "가가가");
        recipe_review1.put("star", 2);
        recipe_review1.put("user", "user1");
        Map<String, Object> recipe_review2 = new HashMap<>();
        recipe_review2.put("content", "나나나");
        recipe_review2.put("star", 2);
        recipe_review2.put("user", "user1");
        recipe_reviews.add(recipe_review1);
        recipe_reviews.add(recipe_review2);
        return recipe_reviews;
    }

    @GetMapping("/users/{userId}/recipe-reviews")
    public List<Map<String, Object>> readAllRecipeReviewsByUser(
            @PathVariable Long userId
    ){
        List<Map<String, Object>> recipe_reviews = new ArrayList<>();
        Map<String, Object> recipe_review1 = new HashMap<>();
        recipe_review1.put("content", "가가가");
        recipe_review1.put("star", 2);
        recipe_review1.put("user", "user1");
        Map<String, Object> recipe_review2 = new HashMap<>();
        recipe_review2.put("content", "나가가");
        recipe_review2.put("star", 2);
        recipe_review2.put("user", "user1");
        recipe_reviews.add(recipe_review1);
        recipe_reviews.add(recipe_review2);
        return recipe_reviews;
    }

    @PutMapping("/recipe-reviews/{recipeReviewId}")
    public void updateRecipeReview(
            @PathVariable Long recipeReviewId,
            String content,
            @Schema(description = "Start value (Byte)", example = "1", type = "integer", format = "int32")
            Byte star
//            @RequestBody RecipeReviewUpdateControllerRequestDto controllerRequestDto
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
    ) throws IOException {

    }

    @DeleteMapping("/recipe-reviews/{recipeReviewId}")
    public void deleteRecipeReview(
            @PathVariable Long recipeReviewId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
    }
}
