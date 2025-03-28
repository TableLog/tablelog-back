package com.tablelog.tablelogback.domain.recipe_process.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 조리과정 API", description = "")
public class RecipeProcessController {
    @PostMapping("/recipe-process/{recipeId}")
    public void createRecipeProcess(
            @PathVariable Long recipeId,
            short sequence,
            String description,
            MultipartFile multipartFile
    ) throws IOException {

    }

    @GetMapping("/recipe-process/{recipeProcessId}")
    public Map<String, Object> readRecipeProcess(
            @PathVariable Long recipeProcessId
    ){
        Map<String, Object> recipe_process1 = new HashMap<>();
        recipe_process1.put("sequence", 1);
        recipe_process1.put("description", "나나나");
        recipe_process1.put("image_url", "https://example.com/image1.jpg");
        recipe_process1.put("user", "user");
        return recipe_process1;
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(recipeProcessService.readRecipeProcess(recipeProcessId));
    }

    @GetMapping("/recipes/{recipeId}/recipe-process")
    public List<Map<String, Object>> readAllRecipeProcessesByRecipeId(
            @PathVariable Long recipeId
    ){
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
        return recipe_processes;
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(recipeProcessService.readAllRecipeProcessesByRecipeId(recipeId));
    }

    @PutMapping("/recipe-process/{recipeProcessId}")
    public void updateRecipeProcess(
            @PathVariable Long recipeProcessId,
            short sequence,
            String description,
            Boolean imageChange,
            MultipartFile multipartFile
//            @RequestBody RecipeProcessUpdateControllerRequestDto controllerRequestDto
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
//            @RequestPart(required = false)MultipartFile multipartFile
    ) throws IOException {
//        RecipeProcessUpdateServiceRequestDto serviceRequestDto =
//                recipeProcessDtoMapper.toRecipeProcessUpdateServiceRequestDto(controllerRequestDto);
//        recipeProcessService.updateRecipeProcess(recipeProcessId, serviceRequestDto
////                multipartFile,userDetails.user()
//        );
//        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/recipe-process/{recipeProcessId}")
    public void deleteRecipeProcess(
            @PathVariable Long recipeProcessId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
//        recipeProcessService.deleteRecipeProcess(recipeProcessId); //user
//        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
