package com.tablelog.tablelogback.domain.recipe.controller;

import com.tablelog.tablelogback.domain.recipe.dto.controller.RecipeCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.controller.RecipeUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeFilterConditionDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeFoodPreviewDto;
import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe.mapper.dto.RecipeDtoMapper;
import com.tablelog.tablelogback.domain.recipe.service.impl.RecipeServiceImpl;
import com.tablelog.tablelogback.domain.recipe_food.dto.controller.RecipeFoodCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.mapper.dto.RecipeFoodDtoMapper;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateRequestDto;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 API", description = "")
public class RecipeController {
    private final RecipeDtoMapper recipeDtoMapper;
    private final RecipeServiceImpl recipeService;
    private final RecipeFoodDtoMapper recipeFoodDtoMapper;

    @Operation(summary = "레시피 생성")
    @PostMapping(value = "/recipes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRecipe(
            @RequestPart(name = "recipeCreateRequestDto") RecipeCreateControllerRequestDto requestDto,
            @RequestPart(name = "recipeImage", required = false) MultipartFile recipeImage,
            @RequestPart(name = "recipeFoodCreateRequestDto")
            List<RecipeFoodCreateControllerRequestDto> recipeFoodDtos,
            @ModelAttribute(name = "recipeProcessCreateRequestDto") RecipeProcessCreateRequestDto rpDtos,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        RecipeCreateServiceRequestDto serviceRequestDto =
                recipeDtoMapper.toRecipeServiceRequestDto(requestDto);
        List<RecipeFoodCreateServiceRequestDto> rfDtos =
                recipeFoodDtos.stream()
                        .map(recipeFoodDtoMapper::toRecipeFoodServiceRequestDto)
                        .collect(Collectors.toList());
        recipeService.createRecipe(serviceRequestDto, recipeImage, rfDtos, rpDtos, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "레시피 단건 조회")
    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<?> readRecipe(
            @PathVariable Long recipeId
    ){
        UserDetailsImpl userDetails = getUserDetails();
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.readRecipe(recipeId, userDetails));
    }

    @Operation(summary = "레시피 단건 조회 레시피 식재료 보기")
    @GetMapping("/recipes/{recipeId}/foods")
    public ResponseEntity<RecipeFoodPreviewDto> readRecipeWithRecipeFood(
            @PathVariable Long recipeId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.readRecipeWithRecipeFood(recipeId));
    }

    @Operation(summary = "레시피 전체 조회 최신순 10개씩", description = "false면 전체 조회")
    @GetMapping("/recipes/latest")
    public ResponseEntity<?> readAllRecipesLatest(
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam int pageNumber
    ) {
        UserDetailsImpl userDetails = getUserDetails();
        return ResponseEntity.status(HttpStatus.OK).body(recipeService.readAllRecipes(pageNumber, userDetails, isPaid));
    }

    @Operation(summary = "레시피 전체 조회 인기순", description = "최신 일주일")
    @GetMapping("/recipes/popular")
    public ResponseEntity<?> readPopularRecipes(
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam int pageNumber
    ) {
        UserDetailsImpl userDetails = getUserDetails();
        return ResponseEntity.status(HttpStatus.OK).body(recipeService.readPopularRecipes(pageNumber, userDetails, isPaid));
    }

    @Operation(summary = "레시피 전체 조회 By 사용자")
    @GetMapping("/users/{userId}/recipes")
    public ResponseEntity<?> readAllRecipesByUser(
            @PathVariable Long userId,
            @RequestParam int pageNumber
    ) {
        UserDetailsImpl userDetails = getUserDetails();
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.readAllRecipeByUser(userId, pageNumber, userDetails));
    }

    @Operation(summary = "내 레시피 전체 조회")
    @GetMapping("/users/me/recipes")
    public ResponseEntity<?> getMyAllRecipes (
            @RequestParam(required = false) Boolean isPaid,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int pageNumber
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.getAllMyRecipes(userDetails, pageNumber, isPaid));
    }

    @Operation(summary = "레시피 전체 조회 By 식재료")
    @GetMapping("/recipes/filter/food")
    public ResponseEntity<?> readAllRecipesByFoodName(
            @RequestParam String keyword,
            @RequestParam int pageNumber
    ) {
        UserDetailsImpl userDetails = getUserDetails();
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.readAllRecipeByFoodName(keyword, pageNumber, userDetails));
    }

    @Operation(summary = "레시피 필터링")
    @GetMapping("/recipes/filter")
    public ResponseEntity<?> filterRecipes (
            @ModelAttribute RecipeFilterConditionDto condition,
            @RequestParam int pageNumber
    ) {
        UserDetailsImpl userDetails = getUserDetails();
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.filterRecipes(condition, pageNumber, userDetails));
    }

    @Operation(summary = "레시피 수정")
    @PutMapping(value = "/recipes/{recipeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRecipe(
            @PathVariable Long recipeId,
            @RequestPart RecipeUpdateControllerRequestDto controllerRequestDto,
            @RequestPart(required = false) MultipartFile multipartFile,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        RecipeUpdateServiceRequestDto serviceRequestDto =
                recipeDtoMapper.toRecipeUpdateServiceDto(controllerRequestDto);
        recipeService.updateRecipe(recipeId, serviceRequestDto, userDetails.user(), multipartFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "레시피 삭제")
    @DeleteMapping("/recipes/{recipeId}")
    public ResponseEntity<?> deleteRecipe(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeService.deleteRecipe(recipeId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private UserDetailsImpl getUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = null;
        if(authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())){
            userDetails = (UserDetailsImpl) authentication.getPrincipal();
        }
        return userDetails;
    }
}
