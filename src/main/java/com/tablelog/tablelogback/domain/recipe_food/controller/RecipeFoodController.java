package com.tablelog.tablelogback.domain.recipe_food.controller;

import com.tablelog.tablelogback.domain.recipe_food.dto.controller.RecipeFoodCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.controller.RecipeFoodUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.mapper.dto.RecipeFoodDtoMapper;
import com.tablelog.tablelogback.domain.recipe_food.service.impl.RecipeFoodServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 식재료 API", description = "")
public class RecipeFoodController {
    private final RecipeFoodDtoMapper recipeFoodDtoMapper;
    private final RecipeFoodServiceImpl recipeFoodService;

    @Operation(summary = "레시피 식재료 생성")
    @PostMapping("/recipes/{recipeId}/recipe-food")
    public ResponseEntity<?> createRecipeFood(
            @PathVariable Long recipeId,
            @RequestBody RecipeFoodCreateControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        RecipeFoodCreateServiceRequestDto serviceRequestDto =
                recipeFoodDtoMapper.toRecipeFoodServiceRequestDto(controllerRequestDto);
        recipeFoodService.createRecipeFood(recipeId, serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "레시피 식재료 단건 조회")
    @GetMapping("/recipes/{recipeId}/recipe-food/{recipeFoodId}")
    public ResponseEntity<RecipeFoodReadAllServiceResponseDto> readRecipeFood(
            @PathVariable Long recipeId,
            @PathVariable Long recipeFoodId
    ){
        RecipeFoodReadAllServiceResponseDto responseDto = recipeFoodService.readRecipeFood(recipeId, recipeFoodId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "레시피 식재료 전체 조회 By 레시피")
    @GetMapping("/recipes/{recipeId}/recipe-food")
    public ResponseEntity<List<RecipeFoodReadAllServiceResponseDto>> readAllRecipeFoodsByRecipeId(
            @PathVariable Long recipeId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(recipeFoodService.readAllRecipeFoodsByRecipeId(recipeId));
    }

    @Operation(summary = "레시피 식재료 수정")
    @PutMapping("/recipes/{recipeId}/recipe-food/{recipeFoodId}")
    public ResponseEntity<?> updateRecipeFood(
            @PathVariable Long recipeId,
            @PathVariable Long recipeFoodId,
            @RequestBody RecipeFoodUpdateControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        RecipeFoodUpdateServiceRequestDto serviceRequestDto =
                recipeFoodDtoMapper.toRecipeFoodUpdateServiceDto(controllerRequestDto);
        recipeFoodService.updateRecipeFood(recipeId, recipeFoodId, serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
