package com.tablelog.tablelogback.domain.recipe_save.controller;

import com.tablelog.tablelogback.domain.recipe_save.service.impl.RecipeSaveServiceImpl;
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
@Tag(name = "레시피 저장 API", description = "")
public class RecipeSaveController {
    private final RecipeSaveServiceImpl recipeSaveService;

    @Operation(summary = "레시피 저장 생성")
    @PostMapping("/recipes/{recipeId}/saves")
    public ResponseEntity<?> addRecipeSave(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeSaveService.createRecipeSave(recipeId, userDetails.user().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "레시피 저장 삭제")
    @DeleteMapping("/recipes/{recipeId}/saves")
    public ResponseEntity<?> deleteRecipeSave(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeSaveService.deleteRecipeSave(recipeId, userDetails.user().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
