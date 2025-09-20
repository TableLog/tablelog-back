package com.tablelog.tablelogback.domain.point_transaction.controller;

import com.tablelog.tablelogback.domain.point_transaction.dto.service.PointTransactionSliceResponseDto;
import com.tablelog.tablelogback.domain.point_transaction.service.impl.PointTransactionServiceImpl;
import com.tablelog.tablelogback.global.enums.PointType;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "포인트 내역 API", description = "")
public class PointTransactionController {
    private final PointTransactionServiceImpl pointTransactionService;

    @Operation(summary = "내 포인트 전체 조회")
    @GetMapping("/users/me/point")
    public ResponseEntity<?> readAllPointTransactionByUser(
            @RequestParam(required = false) PointType pointType,
            @RequestParam int pageNumber,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        PointTransactionSliceResponseDto responseDto = pointTransactionService
                .getAllPointTransactionByUser(pointType, userDetails.user(), pageNumber);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
