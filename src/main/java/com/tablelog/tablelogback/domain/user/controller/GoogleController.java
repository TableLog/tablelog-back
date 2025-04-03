package com.tablelog.tablelogback.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자-구글 API", description = "")
public class GoogleController {
    @GetMapping("/users/google/login/callback")
    public void kakaoLogin(
            @RequestParam("code") String code) {
    }
}
