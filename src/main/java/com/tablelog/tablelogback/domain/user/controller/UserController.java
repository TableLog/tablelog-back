package com.tablelog.tablelogback.domain.user.controller;

import com.tablelog.tablelogback.domain.user.dto.controller.UpdateUserControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserLoginControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserSignUpControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UpdateUserServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserLoginServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.mapper.dto.UserDtoMapper;
import com.tablelog.tablelogback.domain.user.service.UserService;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "사용자 정보", description = "Authorize에 token 넣고 Authorization에는 공백 주면 됨")
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
            @RequestPart(required=false) MultipartFile multipartFile) throws IOException
    {
        UpdateUserServiceRequestDto serviceRequestDto = userDtoMapper
                .toUpdateUserServiceRequestDto(controllerRequestDto);
        userService.updateUser(userDetailsImpl.user(), serviceRequestDto, multipartFile);
        return ResponseEntity.status(HttpStatus.OK).build();
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
