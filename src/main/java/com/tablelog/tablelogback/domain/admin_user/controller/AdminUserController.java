package com.tablelog.tablelogback.domain.admin_user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.admin_user.dto.AdminUserReadResponseDto;
import com.tablelog.tablelogback.domain.admin_user.dto.AdminUserSliceReadResponseDto;
import com.tablelog.tablelogback.domain.admin_user.service.impl.AdminUserServiceImpl;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "관리자 - 유저 API", description = "")
public class AdminUserController {
    private final AdminUserServiceImpl adminUserService;

    @Operation(summary = "회원탈퇴 승인")
    @DeleteMapping("/admin/withdraw/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveDeleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws JacksonException {
        adminUserService.approveDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "유저 요청 전체 조회")
    @GetMapping("/admin/request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserSliceReadResponseDto> getAllAdminUser(
            @RequestParam(required = false) ApplyStatus status,
            @RequestParam int pageNum,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws JacksonException {
        return ResponseEntity.status(HttpStatus.OK).body(
                adminUserService.getAllAdminUser(status, pageNum));
    }

    @Operation(summary = "전문가 인증 승인")
    @PostMapping("/admin/expert/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveExpertVerification(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws JacksonException {
        adminUserService.approveExpertVerification(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
