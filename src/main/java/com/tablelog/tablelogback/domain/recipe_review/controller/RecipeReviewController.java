package com.tablelog.tablelogback.domain.recipe_review.controller;

import com.tablelog.tablelogback.domain.recipe_review.dto.controller.RecipeReviewCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.controller.RecipeReviewUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewReadResponseDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.mapper.dto.RecipeReviewDtoMapper;
import com.tablelog.tablelogback.domain.recipe_review.service.impl.RecipeReviewServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 리뷰 API", description = "")
public class RecipeReviewController {
    private final RecipeReviewDtoMapper recipeReviewDtoMapper;
    private final RecipeReviewServiceImpl recipeReviewService;

    @Operation(summary = "레시피 댓글 생성", description = "상위 댓글이면 prrId는 0, 하위 댓글은 댓글 id")
    @PostMapping("/recipes/{recipeId}/recipe-reviews")
    public ResponseEntity<?> createRecipeReview(
            @PathVariable Long recipeId,
            RecipeReviewCreateControllerRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        RecipeReviewCreateServiceRequestDto serviceRequestDto =
                recipeReviewDtoMapper.toRecipeReviewServiceRequestDto(requestDto);
        recipeReviewService.createRecipeReview(serviceRequestDto, recipeId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "레시피 댓글 단건 조회")
    @GetMapping("/recipes/{recipeId}/recipe-reviews/{recipeReviewId}")
    public ResponseEntity<RecipeReviewReadResponseDto> readRecipeReview(
            @PathVariable Long recipeId,
            @PathVariable Long recipeReviewId
    ){
        UserDetailsImpl userDetails = findUserDetails();
        RecipeReviewReadResponseDto responseDto =
                recipeReviewService.readRecipeReview(recipeId, recipeReviewId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "레시피 댓글 전체 조회 By 레시피")
    @GetMapping("/recipes/{recipeId}/recipe-reviews")
    public ResponseEntity<?> readAllRecipeReviewsByRecipeId(
            @PathVariable Long recipeId,
            @RequestParam int pageNumber
    ){
        UserDetailsImpl userDetails = findUserDetails();
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeReviewService.readAllRecipeReviewsByRecipe(recipeId, pageNumber, userDetails));
    }

    @Operation(summary = "레시피 댓글 전체 조회 By 유저")
    @GetMapping("/users/{userId}/recipe-reviews")
    public ResponseEntity<?> readAllRecipeReviewsByUser(
            @PathVariable Long userId,
            @RequestParam int pageNumber
    ){
        UserDetailsImpl userDetails = findUserDetails();
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeReviewService.readAllRecipeReviewsByUser(userId, pageNumber, userDetails));
    }

    @Operation(summary = "내 레시피 댓글 전체 조회")
    @GetMapping("/users/me/recipe-reviews")
    public ResponseEntity<?> getMyAllRecipeReviews (
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int pageNumber
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeReviewService.getAllMyRecipeReviews(userDetails, pageNumber));
    }

    @Operation(summary = "레시피 댓글 수정")
    @PutMapping("/recipes/{recipeId}/recipe-reviews/{recipeReviewId}")
    public ResponseEntity<?> updateRecipeReview(
            @PathVariable Long recipeId,
            @PathVariable Long recipeReviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody RecipeReviewUpdateControllerRequestDto controllerRequestDto
    ) throws IOException {
        RecipeReviewUpdateServiceRequestDto serviceRequestDto =
                recipeReviewDtoMapper.toRecipeReviewUpdateServiceRequestDto(controllerRequestDto);
        recipeReviewService.updateRecipeReview(serviceRequestDto, recipeId, recipeReviewId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "레시피 댓글 삭제")
    @DeleteMapping("/recipes/{recipeId}/recipe-reviews/{recipeReviewId}")
    public ResponseEntity<?> deleteRecipeReview(
            @PathVariable Long recipeId,
            @PathVariable Long recipeReviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeReviewService.deleteRecipeReview(recipeId, recipeReviewId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private UserDetailsImpl findUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            userDetails = (UserDetailsImpl) authentication.getPrincipal();
        }
        return userDetails;
    }
}
