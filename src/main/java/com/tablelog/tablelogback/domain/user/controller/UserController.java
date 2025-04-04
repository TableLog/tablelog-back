package com.tablelog.tablelogback.domain.user.controller;

import com.tablelog.tablelogback.domain.user.dto.controller.UserLoginControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserSignUpControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserLoginServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import com.tablelog.tablelogback.domain.user.mapper.dto.UserDtoMapper;
import com.tablelog.tablelogback.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Operation(summary = "회원가입")
    @PostMapping(value = "/users/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> signUp(
            @RequestPart UserSignUpControllerRequestDto controllerRequestDto,
            @RequestPart(required = false) MultipartFile multipartFile
    ) throws IOException {
        UserSignUpServiceRequestDto serviceRequestDto = userDtoMapper
                .toUserSignUpServiceRequestDto(controllerRequestDto);
        userService.signUp(serviceRequestDto, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인")
    @PostMapping("/users/login")
    public ResponseEntity<?> login(
            @RequestBody UserLoginControllerRequestDto controllerRequestDto
    ) {
        UserLoginServiceRequestDto serviceRequestDto = userDtoMapper
                .toUserLoginServiceRequestDto(controllerRequestDto);
        userService.login(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
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
