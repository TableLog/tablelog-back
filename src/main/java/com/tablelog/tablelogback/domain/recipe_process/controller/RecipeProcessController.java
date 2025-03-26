package com.tablelog.tablelogback.domain.recipe_process.controller;

import com.tablelog.tablelogback.domain.recipe_process.dto.controller.RecipeProcessCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.controller.RecipeProcessUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.request.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.request.RecipeProcessUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.mapper.dto.RecipeProcessDtoMapper;
import com.tablelog.tablelogback.domain.recipe_process.service.impl.RecipeProcessServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 조리과정 API", description = "")
public class RecipeProcessController {
    private final RecipeProcessDtoMapper recipeProcessDtoMapper;
    private final RecipeProcessServiceImpl recipeProcessService;

    @PostMapping("/recipe-process/{recipeId}")
    public ResponseEntity<?> createRecipeProcess(
            RecipeProcessCreateControllerRequestDto requestDto,
//             @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long recipeId
    ) throws IOException {
        RecipeProcessCreateServiceRequestDto serviceRequestDto =
                recipeProcessDtoMapper.toRecipeProcessCreateServiceRequestDto(requestDto);
        recipeProcessService.createRecipeProcess(serviceRequestDto, recipeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/recipe-process/{recipeProcessId}")
    public ResponseEntity<?> readRecipeProcess(
            @PathVariable Long recipeProcessId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeProcessService.readRecipeProcess(recipeProcessId));
    }

    @GetMapping("/recipe-process/all")
    public ResponseEntity<?> readAllRecipeProcesses(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeProcessService.readAllRecipeProcesses());
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> readAllRecipeProcessesByRecipeId(
            @PathVariable Long recipeId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeProcessService.readAllRecipeProcessesByRecipeId(recipeId));
    }

    @PutMapping("/{recipeProcessId}")
    public ResponseEntity<?> updateRecipeProcess(
            @PathVariable Long recipeProcessId,
            @RequestBody RecipeProcessUpdateControllerRequestDto controllerRequestDto
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
//            @RequestPart(required = false)MultipartFile multipartFile
    ) throws IOException {
        RecipeProcessUpdateServiceRequestDto serviceRequestDto =
                recipeProcessDtoMapper.toRecipeProcessUpdateServiceRequestDto(controllerRequestDto);
        recipeProcessService.updateRecipeProcess(recipeProcessId, serviceRequestDto
//                multipartFile,userDetails.user()
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{recipeProcessId}")
    public ResponseEntity<?> deleteRecipeProcess(
            @PathVariable Long recipeProcessId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeProcessService.deleteRecipeProcess(recipeProcessId); //user
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
