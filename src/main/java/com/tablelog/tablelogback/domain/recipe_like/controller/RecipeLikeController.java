package com.tablelog.tablelogback.domain.recipe_like.controller;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeSliceResponseDto;
import com.tablelog.tablelogback.domain.recipe_like.service.impl.RecipeLikeServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
@Tag(name = "레시피 좋아요 API", description = "")
public class RecipeLikeController {
    private final RecipeLikeServiceImpl recipeLikeService;

    @Operation(summary = "좋아요 생성")
    @PostMapping("/recipes/{recipeId}/likes")
    public ResponseEntity<?> addRecipeLike(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeLikeService.createRecipeLike(recipeId, userDetails.user().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "좋아요 삭제")
    @DeleteMapping("/recipes/{recipeId}/likes")
    public ResponseEntity<?> deleteRecipeLike(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeLikeService.deleteRecipeLike(recipeId, userDetails.user().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "좋아요 단건 조회", description = "내가 좋아요 눌렀는지 T/F")
    @GetMapping("/recipes/{recipeId}/likes/me")
    public ResponseEntity<Boolean> hasRecipeLiked(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.status(HttpStatus.OK).
                body(recipeLikeService.hasRecipeLiked(recipeId, userDetails.user().getId()));
    }

    @Operation(summary = "좋아요 수 전체 조회 By 레시피")
    @GetMapping("/recipes/{recipeId}/likes/count")
    public ResponseEntity<Long> getRecipeLikeCount(
            @PathVariable Long recipeId
    ){
        return ResponseEntity.status(HttpStatus.OK).
                body(recipeLikeService.getRecipeLikeCountByRecipe(recipeId));
    }

    @Operation(summary = "내 레시피 좋아요 전체 조회 최신순")
    @GetMapping("/users/me/recipe-likes")
    public ResponseEntity<RecipeSliceResponseDto> getMyLikedRecipes(
            @RequestParam(required = false) Boolean isPaid,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int pageNumber
    ){
        return ResponseEntity.status(HttpStatus.OK).
                body(recipeLikeService.getMyLikedRecipes(isPaid, userDetails, pageNumber));
    }
}
