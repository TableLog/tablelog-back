package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자 카카오 API", description = "")
public class KakaoController {
    @GetMapping("/users/kakao/login/callback")
    public void kakaoLogin(
            @RequestParam("code") String code)
            throws JacksonException {
//        kakaoService.loginWithKakao(code);
//        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
