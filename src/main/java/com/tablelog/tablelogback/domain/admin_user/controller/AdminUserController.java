package com.tablelog.tablelogback.domain.admin_user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.admin_user.service.impl.AdminUserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "관리자 API", description = "")
public class AdminUserController {
    private final AdminUserServiceImpl adminUserService;

    @Operation(summary = "회원탈퇴 승인")
    @DeleteMapping("/admin/withdraw/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") //이거 된 거 맞음???
    public ResponseEntity<String> approveDeleteUser(@PathVariable Long userId) throws JacksonException {
        adminUserService.approveDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 전문가 인증 승인 -> 요청은 유저에서
}
