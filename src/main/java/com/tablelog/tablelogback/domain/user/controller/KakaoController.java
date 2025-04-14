package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.oauth2.KakaoUserInfoDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.mapper.dto.UserDtoMapper;
import com.tablelog.tablelogback.domain.user.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자 - 카카오 API", description = "")
public class KakaoController {
    private final KakaoService kakaoService;
    private final UserDtoMapper userDtoMapper;

    @Operation(summary = "카카오 연동", description = "카카오 연동 시 정보 불러오기")
    @GetMapping("/users/kakao/login/callback")
    public ResponseEntity<KakaoUserInfoDto> getKakao(
            @RequestParam("code") String code
    ) throws JacksonException {
        KakaoUserInfoDto kakaoUserInfoDto = kakaoService.getKakaoUserInfo(code);
        return ResponseEntity.status(HttpStatus.OK).body(kakaoUserInfoDto);
    }

    @Operation(summary = "카카오 회원가입")
    @PostMapping(value = "users/kakao/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signupWithKakao(
            @RequestPart KakaoUserInfoDto kakaoUserInfoDto,
            @RequestPart(required = false) MultipartFile multipartFile,
            @RequestHeader("Kakao-Access-Token") String kakaoAccessToken
    ) throws IOException {
        kakaoService.signupWithKakao(kakaoUserInfoDto, multipartFile, kakaoAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "카카오 로그인")
    @PostMapping("users/kakao/login")
    public ResponseEntity<UserLoginResponseDto> loginWithKakao(
            @RequestParam("code") String code
    ) throws JacksonException {
        UserLoginResponseDto userLoginResponseDto = kakaoService.loginWithKakao(code);
        return ResponseEntity.status(HttpStatus.OK).body(userLoginResponseDto);
    }

    @Operation(summary = "카카오 연결 끊기")
    @PostMapping("/users/kakao/unlink")
    public ResponseEntity<?> unlinkKakao(
            @RequestHeader("Kakao-Access-Token") String kakaoAccessToken
    ) throws JacksonException {
        kakaoService.unlinkKakao(kakaoAccessToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
