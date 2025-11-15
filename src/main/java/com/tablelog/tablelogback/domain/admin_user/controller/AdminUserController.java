package com.tablelog.tablelogback.domain.admin_user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.admin_user.dto.controller.AdminUserRejectReasonControllerRequestDto;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserRejectReasonServiceRequestDto;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserSliceReadResponseDto;
import com.tablelog.tablelogback.domain.admin_user.mapper.dto.AdminUserDtoMapper;
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
    private final AdminUserDtoMapper adminUserDtoMapper;

//    @Operation(summary = "회원탈퇴 승인")
//    @DeleteMapping("/admin/withdraw/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> approveDeleteUser(
//            @PathVariable Long id,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) throws JacksonException {
//        adminUserService.approveDeleteUser(id);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

    @Operation(summary = "유저 요청 전체 조회")
    @GetMapping("/admin/request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserSliceReadResponseDto> getAllAdminUser(
            @RequestParam(required = false) ApplyStatus status,
            @RequestParam("page") int pageNum,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws JacksonException {
        return ResponseEntity.status(HttpStatus.OK).body(
                adminUserService.getAllAdminUser(status, pageNum));
    }

    @Operation(summary = "전문가 인증 처리", description = "승인과 비슷, 조건 안 맞으면 자동 거절됨")
    @PostMapping("/admin/expert/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reviewExpertVerification(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        adminUserService.reviewExpertVerification(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "전문가 인증 처리 - 거절")
    @PostMapping("/admin/expert/{id}/rejection")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectExpertVerification(
            @PathVariable Long id,
            @RequestBody AdminUserRejectReasonControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        AdminUserRejectReasonServiceRequestDto serviceRequestDto =
                adminUserDtoMapper.toAdminUserRejectReasonServiceRequestDto(controllerRequestDto);
        adminUserService.rejectExpertVerification(id, serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
