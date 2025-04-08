package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.oauth2.GoogleUserInfoDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.service.GoogleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자 - 구글 API", description = "")
public class GoogleController {
    private final GoogleService googleService;

    @Operation(summary = "구글 연동", description = "구글 연동 시 정보 불러오기")
    @GetMapping("/users/google/login/callback")
    public ResponseEntity<GoogleUserInfoDto> getGoogle(
            @RequestParam("code") String code
    ) throws JacksonException {
        GoogleUserInfoDto googleUserInfoDto = googleService.getGoogleUserInfo(code);
        return ResponseEntity.status(HttpStatus.OK).body(googleUserInfoDto);
    }

    @Operation(summary = "구글 회원가입")
    @PostMapping(value = "users/google/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signupWithGoogle(
            @RequestPart GoogleUserInfoDto googleUserInfoDto,
            @RequestPart(required = false) MultipartFile multipartFile,
            @RequestHeader("Google-Access-Token") String googleAccessToken
    ) {
        googleService.signupWithGoogle(googleUserInfoDto, multipartFile, googleAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "구글 로그인")
    @PostMapping("users/google/login")
    public ResponseEntity<UserLoginResponseDto> loginWithGoogle(
            @RequestParam("code") String code
    ) throws JacksonException {
        UserLoginResponseDto userLoginResponseDto = googleService.loginWithGoogle(code);
        return ResponseEntity.status(HttpStatus.OK).body(userLoginResponseDto);
    }

    @Operation(summary = "구글 연결 끊기")
    @PostMapping("/users/google/unlink")
    public ResponseEntity<?> unlinkGoogle(
            @RequestHeader("Google-Access-Token") String googleAccessToken
    ) throws JacksonException {
        googleService.unlinkGoogle(googleAccessToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
