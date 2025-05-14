package com.tablelog.tablelogback.domain.user.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablelog.tablelogback.domain.user.dto.oauth2.SocialUserInfoDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.OAuthAccountResponseDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.OAuthAccount;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.*;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.OAuthAccountRepository;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.impl.UserServiceImpl;
import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshToken;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.jwt.exception.JwtErrorCode;
import com.tablelog.tablelogback.global.jwt.exception.NotFoundSocialRefreshTokenException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    private final OAuthAccountRepository oAuthAccountRepository;
    private final OAuthAccountService oAuthAccountService;
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

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            RestTemplate rt = new RestTemplate();
            ResponseEntity<String> response = rt.postForEntity(
                    "https://oauth2.googleapis.com/token",
                    request,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response.getBody());

        } catch(HttpClientErrorException e){
            throw new InvalidGrantException(UserErrorCode.INVALID_GRANT);
        }
    }

    public Object handleGoogleLogin(String code) throws JsonProcessingException {
        JsonNode jsonNode = getGoogleToken(code);
        String googleAccessToken = jsonNode.get("access_token").asText();
        String googleRefreshToken = null;
        if (jsonNode.get("refresh_token") != null && !jsonNode.get("refresh_token").asText().isEmpty()){
            googleRefreshToken = jsonNode.get("refresh_token").asText();
        }

        SocialUserInfoDto socialUserInfoDto;
        try {
            socialUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);
            httpServletResponse.addCookie(jwtUtil.createCookie("Google-Access-Token", googleAccessToken));
            if(googleRefreshToken != null && !googleRefreshToken.isEmpty()){
                httpServletResponse.addCookie(jwtUtil.createCookie("Google-Refresh-Token", googleRefreshToken));
            }
        } catch (Exception e){
            throw new NotFoundGoogleUserException(UserErrorCode.NOT_FOUND_GOOGLE_USER);
        }

        if(oAuthAccountRepository.existsByProviderAndEmail(socialUserInfoDto.provider(), socialUserInfoDto.email())){
            OAuthAccount oAuthAccount = oAuthAccountRepository
                    .findByProviderAndEmail(socialUserInfoDto.provider(), socialUserInfoDto.email());
            User googleUser = userRepository.findById(oAuthAccount.getUserId())
                    .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            jwtUtil.addTokenToCookie(googleUser, httpServletResponse, "accessToken");
            String refresh = jwtUtil.addTokenToCookie(googleUser, httpServletResponse, "refreshToken");
            RefreshToken refreshToken = new RefreshToken(googleUser.getId(), refresh, timeToLive);
            refreshTokenRepository.save(refreshToken);
            GoogleRefreshToken googleRefresh;
            if(googleRefreshToken != null && !googleRefreshToken.isEmpty()){
                googleRefresh =
                        new GoogleRefreshToken(googleUser.getId(), googleRefreshToken, timeToLive);
            } else {
                googleRefresh = googleRefreshTokenRepository.findById(String.valueOf(googleUser.getId()))
                        .orElseThrow(() -> new NotFoundSocialRefreshTokenException(
                                JwtErrorCode.NOT_FOUND_SOCIAL_REFRESH_TOKEN));
                googleRefreshToken = googleRefresh.getGoogleRefreshToken();
            }
            httpServletResponse.addCookie(jwtUtil.createCookie("Google-Refresh-Token", googleRefreshToken));
            googleRefreshTokenRepository.save(googleRefresh);
            List<OAuthAccountResponseDto> dtos = oAuthAccountService.getAllOAuthAccountDtos(googleUser.getId());
            return userEntityMapper.toUserLoginResponseDto(googleUser, dtos);
        } else {
            return socialUserInfoDto;
        }
    }

    public SocialUserInfoDto getGoogleUserInfoWithAccessToken(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        try {
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
        } catch (HttpClientErrorException e){
            throw new InvalidGrantException(UserErrorCode.INVALID_GRANT);
        }
    }

    public UserLoginResponseDto signupWithGoogle(
            UserSignUpServiceRequestDto serviceRequestDto,
            MultipartFile multipartFile,
            String googleAccessToken
    ) throws IOException {
        User user = userService.signUp(serviceRequestDto, multipartFile);
        // 서버 토큰 저장
        jwtUtil.addTokenToCookie(user, httpServletResponse, "accessToken");
        String refresh = jwtUtil.addTokenToCookie(user, httpServletResponse, "refreshToken");
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        // 구글 토큰 저장
        httpServletResponse.addCookie(jwtUtil.createCookie("Google-Access-Token", googleAccessToken));
        String googleRefresh = jwtUtil.getTokenFromCookie(httpServletRequest, "Google-Refresh-Token");
        GoogleRefreshToken googleRefreshToken = new GoogleRefreshToken(user.getId(), googleRefresh, timeToLive);
        googleRefreshTokenRepository.save(googleRefreshToken);
        httpServletResponse.addCookie(jwtUtil.createCookie("Google-Refresh-Token", googleRefresh));

        // OAuthAccount에 추가
        OAuthAccount oAuthAccount = OAuthAccount.builder()
                .provider(user.getProvider())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
        oAuthAccountRepository.save(oAuthAccount);
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.getAllOAuthAccountDtos(user.getId());
        return userEntityMapper.toUserLoginResponseDto(user, dtos);
    }

    public void refresh(User user) throws JacksonException {
        GoogleRefreshToken googleRefresh = googleRefreshTokenRepository.findById(user.getId().toString())
                .orElseThrow(() -> new NotFoundSocialRefreshTokenException(JwtErrorCode.NOT_FOUND_SOCIAL_REFRESH_TOKEN));
        String googleRefreshToken = googleRefresh.getGoogleRefreshToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", googleRefreshToken);

        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(body, headers);

        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://oauth2.googleapis.com/token",
                    HttpMethod.POST,
                    googleTokenRequest,
                    String.class
            );

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            httpServletResponse.addCookie(
                    jwtUtil.createCookie("Google-Access-Token", jsonNode.get("access_token").asText()));
            if (jsonNode.get("refresh_token") != null && !jsonNode.get("refresh_token").asText().isEmpty()) {
                String newGoogleRefreshToken = jsonNode.get("refresh_token").asText();
                googleRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
                jwtUtil.deleteCookie("Google-Refresh-Token", httpServletResponse);
                GoogleRefreshToken newGoogleRefresh = new GoogleRefreshToken(
                        user.getId(), newGoogleRefreshToken, timeToLive);
                googleRefreshTokenRepository.save(newGoogleRefresh);
                httpServletResponse.addCookie(jwtUtil.createCookie("Google-Refresh-Token", newGoogleRefreshToken));
            }
        } catch (HttpClientErrorException e){
            throw new FailedRefreshGoogleException(UserErrorCode.FAILED_REFRESH_GOOGLE);
        }
    }

    public void unlinkGoogle(String googleAccessToken, HttpServletResponse httpServletResponse) throws JacksonException {
        SocialUserInfoDto socialUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);

        User user = userRepository.findByEmail(socialUserInfoDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + googleAccessToken);

        HttpEntity<?> entity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://oauth2.googleapis.com/revoke?token=" + googleAccessToken,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                googleRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
                userRepository.save(user);
                jwtUtil.deleteCookie("Google-Access-Token", httpServletResponse);
                jwtUtil.deleteCookie("Google-Refresh-Token", httpServletResponse);
            } else {
                log.error("구글 unlink 실패: {}", response.getBody());
                throw new FailedUnlinkGoogleException(UserErrorCode.FAILED_UNLINK_GOOGLE);
            }
        } catch (HttpClientErrorException e){
            throw new InvalidGrantException(UserErrorCode.INVALID_GRANT);
        }
    }

    public Object linkGoogle(String code, User user) throws JacksonException{
        JsonNode jsonNode = getGoogleToken(code);
        String googleAccessToken = jsonNode.get("access_token").asText();
        String googleRefreshToken = null;
        if (jsonNode.get("refresh_token") != null && !jsonNode.get("refresh_token").asText().isEmpty()){
            googleRefreshToken = jsonNode.get("refresh_token").asText();
        }

        SocialUserInfoDto socialUserInfoDto;
        try {
            socialUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);
            httpServletResponse.addCookie(jwtUtil.createCookie("Google-Access-Token", googleAccessToken));
            if(googleRefreshToken != null && !googleRefreshToken.isEmpty()){
                httpServletResponse.addCookie(jwtUtil.createCookie("Google-Refresh-Token", googleRefreshToken));
                GoogleRefreshToken googleRefresh = new GoogleRefreshToken(user.getId(), googleRefreshToken, timeToLive);
                googleRefreshTokenRepository.save(googleRefresh);
            }
        } catch (Exception e){
            throw new NotFoundGoogleUserException(UserErrorCode.NOT_FOUND_GOOGLE_USER);
        }

        if(oAuthAccountRepository.existsByProviderAndEmail(socialUserInfoDto.provider(), socialUserInfoDto.email())){
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }

        OAuthAccount oAuthAccount = OAuthAccount.builder()
                .provider(socialUserInfoDto.provider())
                .email(socialUserInfoDto.email())
                .userId(user.getId())
                .build();
        oAuthAccountRepository.save(oAuthAccount);
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.getAllOAuthAccountDtos(user.getId());
        return userEntityMapper.toUserLoginResponseDto(user, dtos);
    }
}
