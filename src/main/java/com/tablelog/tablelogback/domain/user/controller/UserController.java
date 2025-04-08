package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.controller.UpdateUserControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserLoginControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserSignUpControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.*;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.mapper.dto.UserDtoMapper;
import com.tablelog.tablelogback.domain.user.service.UserService;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @Operation(summary = "로그인", description = "Authorize에 token 넣고 Authorization에는 공백 주면 됨")
    @PostMapping("/users/login")
    public ResponseEntity<?> login(
            @RequestBody UserLoginControllerRequestDto controllerRequestDto
    ) {
        UserLoginServiceRequestDto serviceRequestDto = userDtoMapper
                .toUserLoginServiceRequestDto(controllerRequestDto);
        userService.login(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "사용자 정보")
    @GetMapping("/users")
    public ResponseEntity<UserLoginResponseDto> getUser(
            @RequestHeader("Authorization") String token
    ){
        UserLoginResponseDto responseDto = userService.getUser(token);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "사용자 정보 수정", description = "바꾸는 것만 작성, 안 바꾸면 빈 칸")
    @PutMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(
            @RequestPart UpdateUserControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @RequestPart(required = false) MultipartFile multipartFile) throws IOException
    {
        UpdateUserServiceRequestDto serviceRequestDto = userDtoMapper
                .toUpdateUserServiceRequestDto(controllerRequestDto);
        userService.updateUser(userDetailsImpl.user(), serviceRequestDto, multipartFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String token,
            HttpServletResponse httpServletResponse
    ){
        userService.logout(token, httpServletResponse);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @RequestHeader(value = "Kakao-Access-Token", required = false) String kakaoAccessToken,
            @RequestHeader(value = "Google-Access-Token", required = false) String googleAccessToken,
            HttpServletResponse httpServletResponse
    ) throws JacksonException {
        userService.deleteUser(userDetailsImpl.user(), kakaoAccessToken, googleAccessToken, httpServletResponse);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/users/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @RequestHeader("Refresh-Token") String refreshToken,
            @RequestHeader(value = "Kakao-Refresh-Token", required = false) String kakaoRefreshToken,
            HttpServletResponse httpServletResponse
    ) {
        userService.refreshAccessToken(refreshToken, kakaoRefreshToken, httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 중복 확인")
    @PostMapping("/users/check/email")
    public ResponseEntity<?> isNotDupEmail(
            @RequestBody isNotDupUserEmailServiceRequestDto serviceRequestDto
    ){
        userService.isNotDupUserEmail(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "닉네임 중복 확인")
    @PostMapping("/users/check/nickname")
    public ResponseEntity<?> isNotDupNickname(
            @RequestBody isNotDupUserNickServiceRequestDto serviceRequestDto
    ){
        userService.isNotDupUserNick(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "비밀번호만 변경")
    @PutMapping("users/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @RequestBody UpdatePasswordServiceRequestDto serviceRequestDto
    ){
        userService.updatePassword(userDetailsImpl.user(), serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "이메일 찾기")
    @PostMapping("/users/email")
    public ResponseEntity<?> findEmail(
            @RequestBody findEmailServiceRequestDto serviceRequestDto
    ){
        String email = userService.findEmail(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(email);
    }
}
