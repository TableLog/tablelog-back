package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.oauth2.KakaoUserInfoDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.service.KakaoService;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
