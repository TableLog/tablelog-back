package com.tablelog.tablelogback.domain.user.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablelog.tablelogback.domain.user.dto.oauth2.SocialUserInfoDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.*;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.impl.UserServiceImpl;
import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshToken;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.jwt.oauth2.KakaoRefreshToken;
import com.tablelog.tablelogback.global.jwt.oauth2.KakaoRefreshTokenRepository;
import com.tablelog.tablelogback.global.s3.S3Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {
    @Value("${spring.kakao.client-id}")
    private String clientId;

    @Value("${spring.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.jwt.refresh.expiration-period}")
    private Long timeToLive;

    private Integer refreshTimeToLive;

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse httpServletResponse;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoRefreshTokenRepository kakaoRefreshTokenRepository;
    private final HttpServletRequest httpServletRequest;
    private final S3Provider s3Provider;
    private final UserServiceImpl userService;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";

    private JsonNode getKakaoToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode;
    }

    public SocialUserInfoDto getKakaoUserInfo(String code) throws JsonProcessingException {
        JsonNode jsonNode = getKakaoToken(code);
        String kakaoAccessToken = jsonNode.get("access_token").asText();
        String kakaoRefreshToken = jsonNode.get("refresh_token").asText();
        refreshTimeToLive = jsonNode.get("refresh_token_expires_in").asInt();

        SocialUserInfoDto socialUserInfoDto;
        try{
            socialUserInfoDto = getKakaoUserWithAccessToken(kakaoAccessToken);
        } catch (Exception e){
            throw new NotFoundKakaoUserException(UserErrorCode.NOT_FOUND_USER);
        }
        httpServletResponse.addHeader("Kakao-Access-Token", kakaoAccessToken);
        httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-Refresh-Token", kakaoRefreshToken));
        return socialUserInfoDto;
    }

    public UserLoginResponseDto signupWithKakao(
            UserSignUpServiceRequestDto serviceRequestDto,
            MultipartFile multipartFile,
            String kakaoAccessToken
    ) throws IOException {
        User user = userService.signUp(serviceRequestDto, multipartFile);
        // 서버 토큰 저장
        jwtUtil.addAccessTokenToHeader(user, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(user, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        // 카카오 토큰 저장
        httpServletResponse.addHeader("Kakao-Access-Token", kakaoAccessToken);
        String kakaoRefresh = jwtUtil.getRefreshTokenFromCookie(httpServletRequest, "Kakao-Refresh-Token");
        jwtUtil.deleteCookie("Kakao-Refresh-Token", httpServletResponse);
        KakaoRefreshToken kakaoRefreshToken = new KakaoRefreshToken(user.getId(), kakaoRefresh, refreshTimeToLive);
        kakaoRefreshTokenRepository.save(kakaoRefreshToken);
        return userEntityMapper.toUserLoginResponseDto(user);
    }

    public UserLoginResponseDto loginWithKakao(String code) throws JsonProcessingException {
        JsonNode jsonNode = getKakaoToken(code);
        String kakaoAccessToken = jsonNode.get("access_token").asText();
        String kakaoRefreshToken = jsonNode.get("refresh_token").asText();
        refreshTimeToLive = jsonNode.get("refresh_token_expires_in").asInt();

        SocialUserInfoDto socialUserInfoDto;
        try{
            socialUserInfoDto = getKakaoUserWithAccessToken(kakaoAccessToken);
        } catch (Exception e){
            throw new NotFoundKakaoUserException(UserErrorCode.NOT_FOUND_USER);
        }
        User kakaoUser = userRepository.findByEmail(socialUserInfoDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        // 서버 토큰 저장
        jwtUtil.addAccessTokenToHeader(kakaoUser, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(kakaoUser, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(kakaoUser.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        // 카카오 토큰 저장
        httpServletResponse.addHeader("Kakao-Access-Token", kakaoAccessToken);
        jwtUtil.deleteCookie("Kakao-Refresh-Token", httpServletResponse);
        KakaoRefreshToken kakaoRefresh = new KakaoRefreshToken(kakaoUser.getId(), kakaoRefreshToken, refreshTimeToLive);
        kakaoRefreshTokenRepository.save(kakaoRefresh);
        return userEntityMapper.toUserLoginResponseDto(kakaoUser);
    }

    public void unlinkKakao(String kakaoAccessToken) throws JacksonException {
        SocialUserInfoDto socialUserInfoDto = getKakaoUserWithAccessToken(kakaoAccessToken);
        User user = userRepository.findByEmail(socialUserInfoDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<?> entity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            kakaoRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
            userRepository.save(user);
        } else {
            log.error("카카오 unlink 실패: {}", response.getBody());
            throw new FailedUnlinkKakaoException(UserErrorCode.FAILED_UNLINK_KAKAO);
        }
    }

    private SocialUserInfoDto getKakaoUserWithAccessToken(String kakaoAccessToken) throws JacksonException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("kakao_account").get("profile").get("nickname").asText();
        String name = jsonNode.get("kakao_account").get("name").asText();
        String birth = jsonNode.get("kakao_account").get("birthyear").asText()+"-"
                +jsonNode.get("kakao_account").get("birthday").asText().substring(0,2)+"-"
                +jsonNode.get("kakao_account").get("birthday").asText().substring(2);
        String profileImgUrl;
        if(jsonNode.get("kakao_account").get("profile").get("profile_image_url") != null){
            profileImgUrl = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();
        }
        else {
            profileImgUrl = "";
        }
        return new SocialUserInfoDto(email, nickname, name, birth, profileImgUrl, UserProvider.kakao);
    }

    public void refresh(String kakaoRefreshToken, User user) throws JacksonException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", kakaoRefreshToken);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        httpServletResponse.addHeader("Kakao-Access-Token", jsonNode.get("access_token").asText());
        if (jsonNode.get("refresh_token") != null && !jsonNode.get("refresh_token").asText().isEmpty()) {
            String newKakaoRefreshToken = jsonNode.get("refresh_token").asText();
            kakaoRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
            jwtUtil.deleteCookie("Kakao-Refresh-Token", httpServletResponse);
            KakaoRefreshToken kakaoRefresh = new KakaoRefreshToken(user.getId(), newKakaoRefreshToken, refreshTimeToLive);
            kakaoRefreshTokenRepository.save(kakaoRefresh);
        }
    }
}
