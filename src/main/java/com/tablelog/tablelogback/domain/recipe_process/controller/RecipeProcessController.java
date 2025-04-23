package com.tablelog.tablelogback.domain.recipe_process.controller;

import com.tablelog.tablelogback.domain.recipe_process.dto.controller.RecipeProcessCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.controller.RecipeProcessUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.mapper.dto.RecipeProcessDtoMapper;
import com.tablelog.tablelogback.domain.recipe_process.service.impl.RecipeProcessServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 조리과정 API", description = "")
public class RecipeProcessController {
    private final RecipeProcessDtoMapper recipeProcessDtoMapper;
    private final RecipeProcessServiceImpl recipeProcessService;

    @Operation(summary = "레시피 조리과정 생성")
    @PostMapping(value = "/recipes/{recipeId}/recipe-process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRecipeProcess(
            @PathVariable Long recipeId,
            @RequestPart RecipeProcessCreateControllerRequestDto controllerRequestDto,
            @RequestPart(value = "recipeProcessImages", required = false) List<MultipartFile> recipeProcessImages,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        RecipeProcessCreateServiceRequestDto serviceRequestDto =
                recipeProcessDtoMapper.toRecipeProcessServiceRequestDto(controllerRequestDto);
        recipeProcessService.createRecipeProcess(recipeId, serviceRequestDto, recipeProcessImages, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "레시피 조리과정 단건 조회")
    @GetMapping("/recipes/{recipeId}/recipe-process/{recipeProcessId}")
    public ResponseEntity<RecipeProcessReadAllServiceResponseDto> readRecipeProcess(
            @PathVariable Long recipeId,
            @PathVariable Long recipeProcessId
    ){
        RecipeProcessReadAllServiceResponseDto responseDto =
                recipeProcessService.readRecipeProcess(recipeId, recipeProcessId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "레시피 조리과정 전체 조회 By 레시피")
    @GetMapping("/recipes/{recipeId}/recipe-process")
    public ResponseEntity<List<RecipeProcessReadAllServiceResponseDto>> readAllRecipeProcessByRecipeId(
            @PathVariable Long recipeId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeProcessService.readAllRecipeProcessesByRecipeId(recipeId));
    }
}
