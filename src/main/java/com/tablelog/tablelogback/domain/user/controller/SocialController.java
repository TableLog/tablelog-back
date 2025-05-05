package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.exception.InvalidProviderException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.service.GoogleService;
import com.tablelog.tablelogback.domain.user.service.KakaoService;
import com.tablelog.tablelogback.global.enums.UserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자 - 소셜 API", description = "")
public class SocialController {
    private final KakaoService kakaoService;
    private final GoogleService googleService;

    @Operation(summary = "소셜 로그인")
    @PostMapping("users/social/login")
    public ResponseEntity<Object> loginWithSocial(
            @RequestParam("provider") UserProvider provider,
            @RequestParam("code") String code
    ) throws JacksonException {
        Object objectDto = null;
        if(provider == UserProvider.kakao) {
            objectDto = kakaoService.handleKakaoLogin(code);
        } else if(provider == UserProvider.google) {
            objectDto = googleService.handleGoogleLogin(code);
        } else {
            throw new InvalidProviderException(UserErrorCode.INVALID_PROVIDER);
        }
        return ResponseEntity.status(HttpStatus.OK).body(objectDto);
    }
}
