package com.tablelog.tablelogback.domain.recipe_review.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public void readRecipeReview(
            @PathVariable Long recipeReviewId
    ){
    }

    @GetMapping("/recipes/{recipeId}/recipe-reviews")
    public void readAllRecipeReviewsByRecipeId(
            @PathVariable Long recipeId
    ){
    }

    @GetMapping("/users/{userId}/recipe-reviews")
    public void readAllRecipeReviewsByUser(
            @PathVariable Long userId
    ){
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
