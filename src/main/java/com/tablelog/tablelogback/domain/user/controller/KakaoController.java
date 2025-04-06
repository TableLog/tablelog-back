package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자 카카오 API", description = "")
public class KakaoController {
    private final KakaoService kakaoService;

    @Operation(summary = "카카오 로그인")
    @GetMapping("/users/kakao/login/callback")
    public ResponseEntity<?> kakaoLogin(
            @RequestParam("code") String code
    ) throws JacksonException {
        kakaoService.loginWithKakao(code);
        return ResponseEntity.status(HttpStatus.OK).build();
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
