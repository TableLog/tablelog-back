package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.oauth2.SocialUserInfoDto;
import com.tablelog.tablelogback.domain.user.exception.NotFoundSocialProviderException;
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

    @Operation(summary = "소셜 연동", description = "소셜 연동 시 정보 불러오기")
    @GetMapping("/users/social/login/callback")
    public ResponseEntity<SocialUserInfoDto> getSocial(
            @RequestParam("provider") UserProvider provider,
            @RequestParam("code") String code
    ) throws JacksonException {
        SocialUserInfoDto socialUserInfoDto;
        if(provider == UserProvider.kakao) {
            socialUserInfoDto = kakaoService.getKakaoUserInfo(code);
        } else if(provider == UserProvider.google) {
            socialUserInfoDto = googleService.getGoogleUserInfo(code);
        } else {
            socialUserInfoDto = null;
            throw new NotFoundSocialProviderException(UserErrorCode.NOT_FOUND_SOCIAL_PROVIDER);
        }
        return ResponseEntity.status(HttpStatus.OK).body(socialUserInfoDto);
    }
}
