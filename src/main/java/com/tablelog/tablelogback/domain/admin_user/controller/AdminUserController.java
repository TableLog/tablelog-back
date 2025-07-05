package com.tablelog.tablelogback.domain.admin_user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.admin_user.service.impl.AdminUserServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "관리자 - 유저 API", description = "")
public class AdminUserController {
    private final AdminUserServiceImpl adminUserService;

    @Operation(summary = "회원탈퇴 승인")
    @DeleteMapping("/admin/withdraw/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveDeleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws JacksonException {
        adminUserService.approveDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 전문가 인증 승인 -> 요청은 유저에서
    // 반려도 필요하지 않나?
}
