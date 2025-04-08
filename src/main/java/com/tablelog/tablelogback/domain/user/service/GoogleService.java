package com.tablelog.tablelogback.domain.user.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablelog.tablelogback.domain.user.dto.oauth2.GoogleUserInfoDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.*;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshToken;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.jwt.oauth2.GoogleRefreshToken;
import com.tablelog.tablelogback.global.jwt.oauth2.GoogleRefreshTokenRepository;
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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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

    public GoogleUserInfoDto getGoogleUserInfo(String code) throws JsonProcessingException {
        JsonNode jsonNode = getGoogleToken(code);
        String googleAccessToken = jsonNode.get("access_token").asText();
        String googleRefreshToken = jsonNode.get("refresh_token").asText();

        GoogleUserInfoDto googleUserInfoDto;
        try{
            googleUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);
        } catch (Exception e){
            throw new NotFoundGoogleUserException(UserErrorCode.NOT_FOUND_USER);
        }
        httpServletResponse.addHeader("Google-Access-Token", googleAccessToken);
        httpServletResponse.addCookie(jwtUtil.createCookie("Google-Refresh-Token", googleRefreshToken));
        return googleUserInfoDto;
    }

    public GoogleUserInfoDto getGoogleUserInfoWithAccessToken(String accessToken) throws JsonProcessingException {
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

        return new GoogleUserInfoDto(
                jsonNode.get("email").asText(),
                jsonNode.get("name").asText(),
                jsonNode.get("family_name").asText() + jsonNode.get("given_name").asText(),
                null,
                jsonNode.get("picture").asText()
        );
    }

    public UserLoginResponseDto signupWithGoogle(
            GoogleUserInfoDto googleUserInfoDto,
            MultipartFile multipartFile,
            String googleAccessToken
    ) {
        User user = joinGoogleUser(googleUserInfoDto, multipartFile);
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

    private User joinGoogleUser(GoogleUserInfoDto googleUserInfoDto,
                                MultipartFile multipartFile
    ) {
        String googleEmail = googleUserInfoDto.googleEmail();
        // 구글 가입 여부 확인
        if(userRepository.existsByGoogleEmail(googleEmail)){
            throw new AlreadyExistsUserException(UserErrorCode.ALREADY_EXIST_USER);
        }
        // 중복 가입 확인
        if(userRepository.existsByNameAndBirthday(googleUserInfoDto.name(), googleUserInfoDto.birthday())){
            throw new AlreadyExistsUserException(UserErrorCode.ALREADY_EXIST_USER);
        }
        // 중복 이메일 가입 확인
        if(userRepository.existsByEmail(googleEmail)){
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }
        // 닉네임 중복 확인
        if(userRepository.existsByNickname(googleUserInfoDto.nickname())){
            throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
        }
        User googleUser;
        String uuid = UUID.randomUUID().toString();
        String fileName;
        String fileUrl;
        if (multipartFile == null || multipartFile.isEmpty()){
            googleUser = User.builder()
                    .email(googleEmail)
                    .password(passwordEncoder.encode(uuid))
                    .nickname(googleUserInfoDto.nickname())
                    .name(googleUserInfoDto.name())
                    .birthday(googleUserInfoDto.birthday())
                    .userRole(UserRole.USER)
                    .profileImgUrl(googleUserInfoDto.profileImgUrl())
                    .googleEmail(googleEmail)
                    .build();
        } else {
//            fileName = s3Provider.originalFileName(multipartFile);
//            fileUrl = url + serviceRequestDto.nickname() + SEPARATOR + fileName;
            fileUrl = multipartFile.getOriginalFilename();
            googleUser = User.builder()
                    .email(googleEmail)
                    .password(passwordEncoder.encode(uuid))
                    .nickname(googleUserInfoDto.nickname())
                    .name(googleUserInfoDto.name())
                    .birthday(googleUserInfoDto.birthday())
                    .userRole(UserRole.USER)
                    .profileImgUrl(fileUrl)
                    .googleEmail(googleEmail)
                    .build();
//            fileUrl = user.getFolderName() + SEPARATOR + fileName;
//            s3Provider.createFolder(serviceRequestDto.email());
//            s3Provider.saveFile(multipartFile, fileUrl);
        }
        userRepository.save(googleUser);
        return googleUser;
    }

    public UserLoginResponseDto loginWithGoogle(String code) throws JsonProcessingException {
        JsonNode jsonNode = getGoogleToken(code);
        String googleAccessToken = jsonNode.get("access_token").asText();
//        log.info(jsonNode.get(""))
//        String googleRefreshToken = jsonNode.get("id_token").asText();

        GoogleUserInfoDto googleUserInfoDto;
        try{
            googleUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);
        } catch (Exception e){
            throw new NotFoundGoogleUserException(UserErrorCode.NOT_FOUND_USER);
        }
        User googleUser = userRepository.findByGoogleEmail(googleUserInfoDto.googleEmail())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        // 서버 토큰 저장
        jwtUtil.addAccessTokenToHeader(googleUser, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(googleUser, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(googleUser.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        // 카카오 토큰 저장
        httpServletResponse.addHeader("Google-Access-Token", googleAccessToken);
        jwtUtil.deleteCookie("Google-Refresh-Token", httpServletResponse);
//        GoogleRefreshToken googleRefresh = new GoogleRefreshToken(googleUser.getId(), googleRefreshToken, timeToLive);
//        googleRefreshTokenRepository.save(googleRefresh);
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
        GoogleUserInfoDto googleUserInfoDto = getGoogleUserInfoWithAccessToken(googleAccessToken);

        User user = userRepository.findByGoogleEmail(googleUserInfoDto.googleEmail())
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
            user.deleteGoogleEmail();
            googleRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
            userRepository.save(user);
        } else {
            log.error("구글 unlink 실패: {}", response.getBody());
            throw new FailedUnlinkGoogleException(UserErrorCode.FAILED_UNLINK_GOOGLE);
        }
    }
}
