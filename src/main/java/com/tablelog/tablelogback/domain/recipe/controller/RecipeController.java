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
import java.util.*;

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
            List<RecipeFoodCreateControllerRequestDto> RecipeFoodCreateControllerRequestDto,
            String user
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
    public Map<String, Object> readRecipe(
            @PathVariable Long recipeId
    ){
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("title", "레시피 제목");
        recipe.put("intro", "간단한 설명");
        recipe.put("state", 1);
        recipe.put("recipeCategory", "한식");
        recipe.put("star", 2);
        recipe.put("price", 10000);
        recipe.put("memo", "메모");
        recipe.put("image_url", "https://example.com/image1.jpg");

        // 레시피 조리과정 리스트 생성
        List<Map<String, Object>> recipe_processes = new ArrayList<>();
        Map<String, Object> recipe_process1 = new HashMap<>();
        recipe_process1.put("sequence", 1);
        recipe_process1.put("description", "나나나");
        recipe_process1.put("image_url", "https://example.com/image1.jpg");
        recipe_process1.put("user", "user");
        Map<String, Object> recipe_process2 = new HashMap<>();
        recipe_process2.put("sequence", 2);
        recipe_process2.put("description", "나나나");
        recipe_process2.put("image_url", "https://example.com/image1.jpg");
        recipe_process2.put("user", "user");
        recipe_processes.add(recipe_process1);
        recipe_processes.add(recipe_process2);
        recipe.put("recipe_processes", recipe_processes);

        List<MultipartFile> recipeProcessImages = new LinkedList<>();
        recipe.put("recipeProcessImages", recipeProcessImages);

        // 레시피 식재료 리스트 생성
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
        recipe.put("recipe_foods", recipe_foods);

        recipe.put("user", "user1");
        return recipe;
    }

    @GetMapping("/recipes")
    public List<Map<String, Object>> readAllRecipes(){
        List<Map<String, Object>> recipes = new ArrayList<>();

        Map<String, Object> recipe = new HashMap<>();
        recipe.put("title", "레시피 제목");
        recipe.put("intro", "간단한 설명");
        recipe.put("state", 1);
        recipe.put("recipeCategory", "한식");
        recipe.put("star", 2);
        recipe.put("price", 10000);
        recipe.put("memo", "메모");
        recipe.put("image_url", "https://example.com/image1.jpg");

        // 레시피 조리과정 리스트 생성
        List<Map<String, Object>> recipe_processes = new ArrayList<>();
        Map<String, Object> recipe_process1 = new HashMap<>();
        recipe_process1.put("sequence", 1);
        recipe_process1.put("description", "나나나");
        recipe_process1.put("image_url", "https://example.com/image1.jpg");
        recipe_process1.put("user", "user");
        Map<String, Object> recipe_process2 = new HashMap<>();
        recipe_process2.put("sequence", 2);
        recipe_process2.put("description", "나나나");
        recipe_process2.put("image_url", "https://example.com/image1.jpg");
        recipe_process2.put("user", "user");
        recipe_processes.add(recipe_process1);
        recipe_processes.add(recipe_process2);
        recipe.put("recipe_processes", recipe_processes);

        List<MultipartFile> recipeProcessImages = new LinkedList<>();
        recipe.put("recipeProcessImages", recipeProcessImages);

        // 레시피 식재료 리스트 생성
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
        recipe.put("recipe_foods", recipe_foods);

        recipe.put("user", "user1");

        recipes.add(recipe);
        return recipes;
    }

    @GetMapping("/users/{userId}/recipes")
    public List<Map<String, Object>> readAllRecipesByUser(){
        List<Map<String, Object>> recipes = new ArrayList<>();
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("title", "레시피 제목");
        recipe.put("intro", "간단한 설명");
        recipe.put("state", 1);
        recipe.put("recipeCategory", "한식");
        recipe.put("star", 2);
        recipe.put("price", 10000);
        recipe.put("memo", "메모");
        recipe.put("image_url", "https://example.com/image1.jpg");

        // 레시피 조리과정 리스트 생성
        List<Map<String, Object>> recipe_processes = new ArrayList<>();
        Map<String, Object> recipe_process1 = new HashMap<>();
        recipe_process1.put("sequence", 1);
        recipe_process1.put("description", "나나나");
        recipe_process1.put("image_url", "https://example.com/image1.jpg");
        recipe_process1.put("user", "user");
        Map<String, Object> recipe_process2 = new HashMap<>();
        recipe_process2.put("sequence", 2);
        recipe_process2.put("description", "나나나");
        recipe_process2.put("image_url", "https://example.com/image1.jpg");
        recipe_process2.put("user", "user");
        recipe_processes.add(recipe_process1);
        recipe_processes.add(recipe_process2);
        recipe.put("recipe_processes", recipe_processes);

        List<MultipartFile> recipeProcessImages = new LinkedList<>();
        recipe.put("recipeProcessImages", recipeProcessImages);

        // 레시피 식재료 리스트 생성
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
        recipe.put("recipe_foods", recipe_foods);

        recipe.put("user", "user1");

        recipes.add(recipe);
        return recipes;
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
            MultipartFile multipartFile,
            String user
    ) throws IOException {

    }

    @DeleteMapping("/recipes/{recipeId}")
    public void deleteRecipe(
            @PathVariable Long recipeId,
            String user
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

    }
}
