package com.tablelog.tablelogback.domain.user.controller;

import com.tablelog.tablelogback.global.enums.BoardCategory;
import com.tablelog.tablelogback.global.enums.FoodUnit;
import com.tablelog.tablelogback.global.enums.UserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자 API", description = "")
public class UserController {
    @PostMapping("/users/signup")
    public void createUser(
            String email,
            String password,
            String confirmPassword,
            String nickname,
            UserRole userRole,
            String profileImgUrl,
            String kakaoEmail,
            String googleEmail
    ) throws IOException {

    }

    @PostMapping("/users/longin")
    public void login(
            String email,
            String password
    ) {

    }

    @GetMapping("/users")
    public Map<String, Object> getUser(
            @PathVariable Long user_id
    ) {
        Map<String, Object> user1 = new HashMap<>();
        user1.put("email", "첫 번째 게시글");
        user1.put("password", "보안으로 들어갈 예정");
        user1.put("nickname", "익명");
        user1.put("profileImgUrl", "https://example.com/image1.jpg");
        user1.put("kakaoEmail", "소셜 연동시");
        user1.put("googleEmail", "소셜 연동시");
        return user1;
    }

    @PutMapping("/users")
    public void updateUser(
//            String email,
            String newPassword,
            String confirmPassword,
            String nickname,
            Boolean imageChange,
            String kakaoEmail,
            String googleEmail,
            MultipartFile profileImage
    )throws IOException{

    }

    @PostMapping("/users/logout")
    public void logout(
            @RequestHeader("Authorization") String token
    ){

    }

    @DeleteMapping("/users")
    public void deleteUser(
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

    }

    @PostMapping("/users/refresh")
    public void refreshAccessToken(
//            @RequestHeader("Cookie")
            String refreshToken
    ) {

    }
}
