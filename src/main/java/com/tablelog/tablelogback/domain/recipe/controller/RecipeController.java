package com.tablelog.tablelogback.domain.recipe.controller;

import com.tablelog.tablelogback.domain.recipe_food.controller.dto.controller.RecipeFoodCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.controller.dto.controller.RecipeProcessCreateControllerRequestDto;
import com.tablelog.tablelogback.global.enums.RecipeCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 API", description = "")
public class RecipeController {
//    private final RecipeDtoMapper recipeDtoMapper;
//    private final RecipeServiceImpl recipeService;

    @PostMapping("/recipes")
    public void createRecipe(
            String title,
            String intro,
            int state,
            RecipeCategory recipeCategory,
            @Schema(description = "Start value (Byte)", example = "1", type = "integer", format = "int32")
            Byte star,
            Integer price,
            String memo,
            MultipartFile recipeImage,
            List<RecipeProcessCreateControllerRequestDto> RecipeProcessCreateControllerRequestDto,
            List<MultipartFile> recipeProcessImage,
            List<RecipeFoodCreateControllerRequestDto> RecipeFoodCreateControllerRequestDto
//            @Valid @RequestPart(name = "recipeCreateRequestDto")
//            @RequestBody RecipeCreateControllerRequestDto controllerRequestDto
//            @RequestPart(name = "recipeCreateImage",required = false) MultipartFile recipeImage,
//            @AuthenticationPrincipal UserDetailsImpl userDetails)
    ) throws IOException {
//        RecipeCreateServiceRequestDto serviceRequestDto = recipeDtoMapper.toRecipeServiceRequestDto(
//                controllerRequestDto);
//        recipeService.createRecipe(serviceRequestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/recipes/{recipeId}")
    public void readRecipe(
            @PathVariable Long recipeId
    ){

    }

    @GetMapping("/recipes")
    public void readAllRecipes(){

    }

    @GetMapping("/users/{userId}/recipes")
    public void readAllRecipesByUser(){

    }

    @PutMapping("/recipes/{recipeId}")
    public void updateRecipe(
            String title,
            String intro,
            int state,
            RecipeCategory recipeCategory,
            @Schema(description = "Start value (Byte)", example = "1", type = "integer", format = "int32")
            Byte star,
            Integer price,
            String memo,
            Boolean imageChange,
            MultipartFile multipartFile
    ) throws IOException {

    }

    @DeleteMapping("/recipes/{recipeId}")
    public void deleteRecipe(
            @PathVariable Long recipeId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

    }
}
