package com.tablelog.tablelogback.domain.user_license.controller;

import com.tablelog.tablelogback.domain.user_license.dto.controller.UserLicenseCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user_license.mapper.dto.UserLicenseDtoMapper;
import com.tablelog.tablelogback.domain.user_license.service.impl.UserLicenseServiceImpl;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "전문가 인증 API", description = "")
public class UserLicenseController {
    private final UserLicenseDtoMapper userLicenseDtoMapper;
    private final UserLicenseServiceImpl userLicenseService;

    @Operation(summary = "전문가 인증 라이센스 생성")
    @PostMapping(value = "/users/license", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUserLicense(
            @RequestPart UserLicenseCreateControllerRequestDto controllerRequestDto,
            @RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile,  /// 테스트시에만
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        UserLicenseCreateServiceRequestDto serviceRequestDto =
                userLicenseDtoMapper.toUserLicenseCreateServiceDto(controllerRequestDto);
        userLicenseService.createUserLicense(serviceRequestDto, userDetails.user(), multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // get 전부

    // get 사업자만

    // get 특허증만
}
