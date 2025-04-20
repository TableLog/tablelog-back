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
import com.tablelog.tablelogback.global.jwt.oauth2.GoogleRefreshToken;
import com.tablelog.tablelogback.global.jwt.oauth2.GoogleRefreshTokenRepository;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleService {
    @Value("${spring.google.client-id}")
    private String clientId;

    @Value("${spring.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.google.client_secret}")
    private String clientSecret;

    @Value("${spring.jwt.refresh.expiration-period}")
    private Long timeToLive;

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse httpServletResponse;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpServletRequest httpServletRequest;
    private final GoogleRefreshTokenRepository googleRefreshTokenRepository;
    private final S3Provider s3Provider;
    private final UserServiceImpl userService;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";

    private JsonNode getGoogleToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", decodedCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response.getBody());
    }

    public SocialUserInfoDto getGoogleUserInfo(String code) throws JsonProcessingException {
        JsonNode jsonNode = getGoogleToken(code);
        String googleAccessToken = jsonNode.get("access_token").asText();
        String googleRefreshToken = jsonNode.get("refresh_token").asText();

        SocialUserInfoDto socialUserInfoDto;
        try{
            socialUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);
        } catch (Exception e){
            throw new NotFoundGoogleUserException(UserErrorCode.NOT_FOUND_USER);
        }
        httpServletResponse.addHeader("Google-Access-Token", googleAccessToken);
        httpServletResponse.addCookie(jwtUtil.createCookie("Google-Refresh-Token", googleRefreshToken));
        return socialUserInfoDto;
    }

    public SocialUserInfoDto getGoogleUserInfoWithAccessToken(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                entity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        return new SocialUserInfoDto(
                jsonNode.get("email").asText(),
                jsonNode.get("name").asText(),
                jsonNode.get("family_name").asText() + jsonNode.get("given_name").asText(),
                null,
                jsonNode.get("picture").asText(),
                UserProvider.google
        );
    }

    public UserLoginResponseDto signupWithGoogle(
            UserSignUpServiceRequestDto serviceRequestDto,
            MultipartFile multipartFile,
            String googleAccessToken
    ) throws IOException {
        User user = userService.signUp(serviceRequestDto, multipartFile);
        // 서버 토큰 저장
        jwtUtil.addAccessTokenToHeader(user, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(user, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        // 구글 토큰 저장
        httpServletResponse.addHeader("Google-Access-Token", googleAccessToken);
        String googleRefresh = jwtUtil.getRefreshTokenFromCookie(httpServletRequest, "Google-Refresh-Token");
        jwtUtil.deleteCookie("Google-Refresh-Token", httpServletResponse);
        GoogleRefreshToken googleRefreshToken = new GoogleRefreshToken(user.getId(), googleRefresh, timeToLive);
        googleRefreshTokenRepository.save(googleRefreshToken);
        return userEntityMapper.toUserLoginResponseDto(user);
    }

    public UserLoginResponseDto loginWithGoogle(String code) throws JsonProcessingException {
        JsonNode jsonNode = getGoogleToken(code);
        String googleAccessToken = jsonNode.get("access_token").asText();

        SocialUserInfoDto socialUserInfoDto;
        try{
            socialUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);
        } catch (Exception e){
            throw new NotFoundGoogleUserException(UserErrorCode.NOT_FOUND_USER);
        }
        User googleUser = userRepository.findByEmail(socialUserInfoDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        // 서버 토큰 저장
        jwtUtil.addAccessTokenToHeader(googleUser, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(googleUser, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(googleUser.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        // 구글 토큰 저장
        httpServletResponse.addHeader("Google-Access-Token", googleAccessToken);
        jwtUtil.deleteCookie("Google-Refresh-Token", httpServletResponse);
        return userEntityMapper.toUserLoginResponseDto(googleUser);
    }

    public void refresh(String googleRefreshToken, User user) throws JacksonException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", googleRefreshToken);

        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(body, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        httpServletResponse.addHeader("Google-Access-Token", jsonNode.get("access_token").asText());
        log.info(jsonNode.get("access_token").asText());

        if (jsonNode.get("refresh_token") != null && !jsonNode.get("refresh_token").asText().isEmpty()) {
            String newGoogleRefreshToken = jsonNode.get("refresh_token").asText();
            googleRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
            jwtUtil.deleteCookie("Google-Refresh-Token", httpServletResponse);
            GoogleRefreshToken googleRefresh = new GoogleRefreshToken(user.getId(), newGoogleRefreshToken, timeToLive);
            googleRefreshTokenRepository.save(googleRefresh);
        }
    }

    public void unlinkGoogle(String googleAccessToken) throws JacksonException {
        SocialUserInfoDto socialUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);

        User user = userRepository.findByEmail(socialUserInfoDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + googleAccessToken);

        HttpEntity<?> entity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/revoke?token=" + googleAccessToken,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            googleRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
            userRepository.save(user);
        } else {
            log.error("구글 unlink 실패: {}", response.getBody());
            throw new FailedUnlinkGoogleException(UserErrorCode.FAILED_UNLINK_GOOGLE);
        }
    }
}
