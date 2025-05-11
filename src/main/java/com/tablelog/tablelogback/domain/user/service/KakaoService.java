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
import com.tablelog.tablelogback.global.jwt.oauth2.KakaoRefreshToken;
import com.tablelog.tablelogback.global.jwt.oauth2.KakaoRefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    private final HttpServletResponse httpServletResponse;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoRefreshTokenRepository kakaoRefreshTokenRepository;
    private final HttpServletRequest httpServletRequest;
    private final UserServiceImpl userService;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final OAuthAccountService oAuthAccountService;
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
        try {
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

        } catch(HttpClientErrorException e){
            throw new InvalidGrantException(UserErrorCode.INVALID_GRANT);
        }
    }

    public Object handleKakaoLogin(String code) throws JsonProcessingException {
        JsonNode jsonNode = getKakaoToken(code);
        String kakaoAccessToken = jsonNode.get("access_token").asText();
        String kakaoRefreshToken = jsonNode.get("refresh_token").asText();
        refreshTimeToLive = jsonNode.get("refresh_token_expires_in").asInt();

        SocialUserInfoDto socialUserInfoDto;
        try {
            socialUserInfoDto = getKakaoUserWithAccessToken(kakaoAccessToken);
            httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-Access-Token", kakaoAccessToken));
            httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-Refresh-Token", kakaoRefreshToken));
        } catch (Exception e) {
            throw new NotFoundKakaoUserException(UserErrorCode.NOT_FOUND_KAKAO_USER);
        }

        // 회원 존재
        if(userRepository.existsByUserNameAndBirthday(socialUserInfoDto.userName(), socialUserInfoDto.birthday())){
            User kakaoUser = userRepository
                    .findByUserNameAndBirthday(socialUserInfoDto.userName(), socialUserInfoDto.birthday())
                    .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            // 기존 회원이면 로그인
            if(oAuthAccountRepository.existsByEmail(socialUserInfoDto.email())) {
                jwtUtil.addTokenToCookie(kakaoUser, httpServletResponse, "accessToken");
                String refresh = jwtUtil.addTokenToCookie(kakaoUser, httpServletResponse, "refreshToken");
                RefreshToken refreshToken = new RefreshToken(kakaoUser.getId(), refresh, timeToLive);
                refreshTokenRepository.save(refreshToken);
                KakaoRefreshToken kakaoRefresh =
                        new KakaoRefreshToken(kakaoUser.getId(), kakaoRefreshToken, refreshTimeToLive);
                kakaoRefreshTokenRepository.save(kakaoRefresh);
                httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-Refresh-Token", kakaoRefreshToken));
            } else { // 추후 연동 시
                OAuthAccount oAuthAccount = OAuthAccount.builder()
                        .provider(socialUserInfoDto.provider())
                        .email(socialUserInfoDto.email())
                        .userId(kakaoUser.getId())
                        .build();
                oAuthAccountRepository.save(oAuthAccount);
            }
            List<OAuthAccountResponseDto> dtos = oAuthAccountService.getAllOAuthAccountDtos(kakaoUser.getId());
            return userEntityMapper.toUserLoginResponseDto(kakaoUser, dtos);
        } else {
            return socialUserInfoDto;
        }
    }

    public UserLoginResponseDto signupWithKakao(
            UserSignUpServiceRequestDto serviceRequestDto,
            MultipartFile multipartFile,
            String kakaoAccessToken
    ) throws IOException {
        User user = userService.signUp(serviceRequestDto, multipartFile);
        // 서버 토큰 저장
        jwtUtil.addTokenToCookie(user, httpServletResponse, "accessToken");
        String refresh = jwtUtil.addTokenToCookie(user, httpServletResponse, "refreshToken");
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        // 카카오 토큰 저장
        httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-Access-Token", kakaoAccessToken));
        String kakaoRefresh = jwtUtil.getTokenFromCookie(httpServletRequest, "Kakao-Refresh-Token");
        jwtUtil.deleteCookie("Kakao-Refresh-Token", httpServletResponse);
        KakaoRefreshToken kakaoRefreshToken = new KakaoRefreshToken(user.getId(), kakaoRefresh, refreshTimeToLive);
        kakaoRefreshTokenRepository.save(kakaoRefreshToken);
        httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-Refresh-Token", kakaoRefresh));

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

    public void unlinkKakao(String kakaoAccessToken, HttpServletResponse httpServletResponse) throws JacksonException {
        SocialUserInfoDto socialUserInfoDto = getKakaoUserWithAccessToken(kakaoAccessToken);
        User user = userRepository.findByEmail(socialUserInfoDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<?> entity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/unlink",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                oAuthAccountRepository.deleteAllByUserId(user.getId());
                kakaoRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
                userRepository.save(user);
                jwtUtil.deleteCookie("Kakao-Access-Token", httpServletResponse);
                jwtUtil.deleteCookie("Kakao-Refresh-Token", httpServletResponse);
            } else {
                log.error("카카오 unlink 실패: {}", response.getBody());
                throw new FailedUnlinkKakaoException(UserErrorCode.FAILED_UNLINK_KAKAO);
            }
        } catch(HttpClientErrorException e){
            throw new InvalidGrantException(UserErrorCode.INVALID_GRANT);
        }
    }

    private SocialUserInfoDto getKakaoUserWithAccessToken(String kakaoAccessToken) throws JacksonException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        try {
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
            String birth = jsonNode.get("kakao_account").get("birthyear").asText() + "-"
                    + jsonNode.get("kakao_account").get("birthday").asText().substring(0, 2) + "-"
                    + jsonNode.get("kakao_account").get("birthday").asText().substring(2);
            String profileImgUrl;
            if (jsonNode.get("kakao_account").get("profile").get("profile_image_url") != null) {
                profileImgUrl = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();
            } else {
                profileImgUrl = "";
            }
            return new SocialUserInfoDto(email, nickname, name, birth, profileImgUrl, UserProvider.kakao);
        } catch(HttpClientErrorException e) {
            throw new InvalidGrantException(UserErrorCode.INVALID_GRANT);
        }
    }

    public void refresh(String kakaoRefreshToken, User user) throws JacksonException {
        kakaoRefreshTokenRepository.findByKakaoRefreshToken(kakaoRefreshToken)
                .orElseThrow(() -> new NotFoundSocialRefreshTokenException(JwtErrorCode.NOT_FOUND_SOCIAL_REFRESH_TOKEN));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", kakaoRefreshToken);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            httpServletResponse.addCookie(
                    jwtUtil.createCookie("Kakao-Access-Token", jsonNode.get("access_token").asText()));
            if (jsonNode.get("refresh_token") != null && !jsonNode.get("refresh_token").asText().isEmpty()) {
                String newKakaoRefreshToken = jsonNode.get("refresh_token").asText();
                kakaoRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
                jwtUtil.deleteCookie("Kakao-Refresh-Token", httpServletResponse);
                KakaoRefreshToken kakaoRefresh =
                        new KakaoRefreshToken(user.getId(), newKakaoRefreshToken, refreshTimeToLive);
                kakaoRefreshTokenRepository.save(kakaoRefresh);
                httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-Refresh-Token", newKakaoRefreshToken));
            }
        } catch (HttpClientErrorException e){
            throw new FailedRefreshKakaoException(UserErrorCode.FAILED_REFRESH_KAKAO);
        }
    }
}
