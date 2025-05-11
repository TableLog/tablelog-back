package com.tablelog.tablelogback.domain.user.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.controller.UpdateUserControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserLoginControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.controller.UserSignUpControllerRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.*;
import com.tablelog.tablelogback.domain.user.dto.service.response.FindEmailResponseDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.mapper.dto.UserDtoMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.GoogleService;
import com.tablelog.tablelogback.domain.user.service.KakaoService;
import com.tablelog.tablelogback.domain.user.service.UserService;
import com.tablelog.tablelogback.global.enums.UserProvider;
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
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final UserRepository userRepository;

    @Operation(summary = "회원가입")
    @PostMapping(value = "/users/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> signUp(
            @RequestPart UserSignUpControllerRequestDto controllerRequestDto,
            @RequestPart(required = false) MultipartFile multipartFile,
            @RequestHeader(required = false) String socialAccessToken
    ) throws IOException {
        UserSignUpServiceRequestDto serviceRequestDto = userDtoMapper
                .toUserSignUpServiceRequestDto(controllerRequestDto);;
        if(controllerRequestDto.provider() == UserProvider.local){
            userService.signUp(serviceRequestDto, multipartFile);
        } else if(controllerRequestDto.provider() == UserProvider.kakao){
            kakaoService.signupWithKakao(serviceRequestDto, multipartFile, socialAccessToken);
        } else if(controllerRequestDto.provider() == UserProvider.google){
            googleService.signupWithGoogle(serviceRequestDto, multipartFile, socialAccessToken);
        }
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

    @Operation(summary = "사용자 정보", description = "스웨거에서는 공백 한 칸, Authorize에 따로 저장 X")
    @GetMapping("/users")
    public ResponseEntity<UserLoginResponseDto> getUser(
            @CookieValue("accessToken") String token
    ){
        UserLoginResponseDto responseDto = userService.getUser(token);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "사용자 정보 수정", description = "바꾸는 것만 작성, 안 바꾸면 빈 칸")
    @PutMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(
            @RequestPart UpdateUserControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @RequestPart(required = false) MultipartFile multipartFile,
            HttpServletResponse httpServletResponse) throws IOException
    {
        UpdateUserServiceRequestDto serviceRequestDto = userDtoMapper
                .toUpdateUserServiceRequestDto(controllerRequestDto);
        userService.updateUser(userDetailsImpl.user(), serviceRequestDto, multipartFile, httpServletResponse);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(
            @CookieValue("accessToken") String token,
            HttpServletResponse httpServletResponse
    ){
        userService.logout(token, httpServletResponse);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @RequestHeader(value = "Social-Access-Token", required = false) String socialAccessToken,
            HttpServletResponse httpServletResponse
    ) throws JacksonException {
        if(userDetailsImpl.user().getProvider() == UserProvider.kakao){
            kakaoService.unlinkKakao(socialAccessToken, httpServletResponse);
        } else if(userDetailsImpl.user().getProvider() == UserProvider.google){
            googleService.unlinkGoogle(socialAccessToken, httpServletResponse);
        }
        userService.deleteUser(userDetailsImpl.user(), httpServletResponse);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "토큰 갱신", description = "스웨거에서 refreshToken은 빈 칸 하나로 가능")
    @PostMapping("/users/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue("refreshToken") String refreshToken,
            @RequestHeader(value = "Social-Refresh-Token", required = false) String socialRefreshToken,
            HttpServletResponse httpServletResponse
    ) throws JacksonException {
        UserLoginResponseDto responseDto =
                userService.refreshAccessToken(refreshToken, socialRefreshToken, httpServletResponse);
        User user = userRepository.findByEmail(responseDto.email())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        if(responseDto.provider() == UserProvider.kakao){
            kakaoService.refresh(socialRefreshToken, user);
        } else if(responseDto.provider() == UserProvider.google){
            googleService.refresh(user);
        }
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
            @RequestBody UpdatePasswordServiceRequestDto serviceRequestDto
    ){
        userService.updatePassword(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "이메일 찾기")
    @PostMapping("/users/email")
    public ResponseEntity<?> findEmail(
            @RequestBody findEmailServiceRequestDto serviceRequestDto
    ){
        FindEmailResponseDto responseDto = userService.findEmail(serviceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
