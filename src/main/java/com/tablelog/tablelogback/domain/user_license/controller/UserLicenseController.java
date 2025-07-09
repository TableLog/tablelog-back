package com.tablelog.tablelogback.domain.user_license.controller;

import com.tablelog.tablelogback.domain.user_license.dto.controller.UserLicenseCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCountResponseDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseSliceResponseDto;
import com.tablelog.tablelogback.domain.user_license.mapper.dto.UserLicenseDtoMapper;
import com.tablelog.tablelogback.domain.user_license.service.impl.UserLicenseServiceImpl;
import com.tablelog.tablelogback.global.enums.LicenseType;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "전문가 인증용 라이센스 API", description = "")
public class UserLicenseController {
    private final UserLicenseDtoMapper userLicenseDtoMapper;
    private final UserLicenseServiceImpl userLicenseService;

    @Operation(summary = "라이센스 생성")
    @PostMapping(value = "/users/license", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUserLicense(
            @RequestPart UserLicenseCreateControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        UserLicenseCreateServiceRequestDto serviceRequestDto =
                userLicenseDtoMapper.toUserLicenseCreateServiceDto(controllerRequestDto);
        userLicenseService.createUserLicense(serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "전문가 인증 라이센스 전체 조회 By User", description = "licenseType null이면 전체 조회")
    @GetMapping("/users/license")
    public ResponseEntity<UserLicenseSliceResponseDto> getAllUserLicensesByUser(
            @RequestParam(required = false) LicenseType licenseType,
            @RequestParam("page") Integer pageNumber,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserLicenseSliceResponseDto responseDto = null;
        if (licenseType == null) {
            responseDto = userLicenseService.getAllUserLicenseByUser(pageNumber, userDetails.user());
        } else {
            responseDto = userLicenseService.getAllUserLicensesByUserAndLicenseType(licenseType, pageNumber, userDetails.user());
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "라이센스 개수 조회 By User")
    @GetMapping("/users/license/count")
    public ResponseEntity<UserLicenseCountResponseDto> getUserLicenseCountByUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserLicenseCountResponseDto responseDto = userLicenseService.getCountByUser(userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "관리자가 전문가 승인 위해 유저의 라이센스 조회")
    @GetMapping("/admin/expert/license/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserLicenseSliceResponseDto> getAllUserLicenseByUserId(
            @PathVariable Long userId,
            @RequestParam("page") Integer pageNumber,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserLicenseSliceResponseDto responseDto = userLicenseService.getAllUserLicenseByUserId(userId, pageNumber);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // get 단건은?
}
