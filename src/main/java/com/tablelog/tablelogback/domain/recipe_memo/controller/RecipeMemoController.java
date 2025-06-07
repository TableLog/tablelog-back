package com.tablelog.tablelogback.domain.recipe_memo.controller;

import com.tablelog.tablelogback.domain.recipe_memo.dto.RecipeMemoCreateDto;
import com.tablelog.tablelogback.domain.recipe_memo.dto.RecipeMemoResponseDto;
import com.tablelog.tablelogback.domain.recipe_memo.service.impl.RecipeMemoServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 메모 API", description = "")
public class RecipeMemoController {
    private final RecipeMemoServiceImpl recipeMemoService;

    @Operation(summary = "레시피 메모 생성")
    @PostMapping("/recipes/{recipeId}/memos")
    public ResponseEntity<?> createRecipeMemo(
            @PathVariable Long recipeId,
            @RequestBody RecipeMemoCreateDto memoCreateDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        recipeMemoService.createRecipeMemo(recipeId, userDetails.user(), memoCreateDto.memo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "레시피 메모 단건 조회")
    @GetMapping("/recipes/{recipeId}/memos")
    public ResponseEntity<?> createRecipeMemo(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        RecipeMemoResponseDto recipeMemo = recipeMemoService.getRecipeMemo(recipeId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).body(recipeMemo);
    }
}
