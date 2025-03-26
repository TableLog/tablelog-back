package com.tablelog.tablelogback.domain.recipe_process.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public void readRecipeProcess(
            @PathVariable Long recipeProcessId
    ){
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(recipeProcessService.readRecipeProcess(recipeProcessId));
    }

    @GetMapping("/recipes/{recipeId}/recipe-process")
    public void readAllRecipeProcessesByRecipeId(
            @PathVariable Long recipeId
    ){
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
