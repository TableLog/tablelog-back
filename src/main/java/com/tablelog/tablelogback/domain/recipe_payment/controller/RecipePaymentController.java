package com.tablelog.tablelogback.domain.recipe_payment.controller;

import com.tablelog.tablelogback.domain.recipe_payment.service.impl.RecipePaymentServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "레시피 구매 API", description = "")
public class RecipePaymentController {
    private final RecipePaymentServiceImpl recipePaymentService;

    @Operation(summary = "레시피 포인트로 구매")
    @PostMapping("/recipes/{recipeId}/payments")
    public ResponseEntity<?> purchaseRecipeWithPoints(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        recipePaymentService.createRecipePayment(recipeId, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @Operation(summary = "레시피 구매 내역 조회")
//    @GetMapping("/recipes/{recipeId}/payments")
//    public ResponseEntity<?> getaymentsRecipeWithPoints(
//            @PathVariable Long recipeId,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) throws IOException {
//        recipePaymentService.createRecipePayment(recipeId, userDetails.user());
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
}
