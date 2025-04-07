package com.tablelog.tablelogback.domain.user.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablelog.tablelogback.domain.user.dto.oauth2.KakaoUserInfoDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.*;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshToken;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
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

import java.util.UUID;

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

    public KakaoUserInfoDto getKakaoUserInfo(String code) throws JsonProcessingException {
        JsonNode jsonNode = getKakaoToken(code);
        String kakaoAccessToken = jsonNode.get("access_token").asText();
        String kakaoRefreshToken = jsonNode.get("refresh_token").asText();
        refreshTimeToLive = jsonNode.get("refresh_token_expires_in").asInt();

        KakaoUserInfoDto kakaoUserInfoDto;
        try{
            kakaoUserInfoDto = getKakaoUserWithAccessToken(kakaoAccessToken);
        } catch (Exception e){
            throw new NotFoundKakaoUserException(UserErrorCode.NOT_FOUND_USER);
        }
        httpServletResponse.addHeader("Kakao-Access-Token", kakaoAccessToken);
        httpServletResponse.addCookie(jwtUtil.createCookie("Kakao-refresh-Token", kakaoRefreshToken));
        return kakaoUserInfoDto;
    }

    public UserLoginResponseDto signupWithKakao(
            KakaoUserInfoDto kakaoUserInfoDto,
            MultipartFile multipartFile,
            HttpServletRequest request
    ) {
        User user = joinKakaoUser(kakaoUserInfoDto, multipartFile);
        // 서버 토큰 저장
        jwtUtil.addAccessTokenToHeader(user, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(user, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        String kakaoRefreshToken = jwtUtil.getRefreshTokenFromCookie(request, "Kakao-refresh-Token");
        RefreshToken kakaoRefresh = new RefreshToken(user.getId(), kakaoRefreshToken, refreshTimeToLive.longValue());
        refreshTokenRepository.save(kakaoRefresh);
        return userEntityMapper.toUserLoginResponseDto(user);
    }

    private User joinKakaoUser(KakaoUserInfoDto kakaoUserInfoDto,
                               MultipartFile multipartFile
    ) {
        String kakaoEmail = kakaoUserInfoDto.kakaoEmail();
        User kakaoUser = userRepository.findByKakaoEmail(kakaoEmail).orElse(null);
        String uuid = UUID.randomUUID().toString();
        if(kakaoUser == null){
            // 중복 가입 확인
            if(userRepository.existsByNameAndBirthday(kakaoUserInfoDto.name(), kakaoUserInfoDto.birthday())){
                throw new AlreadyExistsUserException(UserErrorCode.ALREADY_EXIST_USER);
            }
            // 닉네임 중복 확인
            if(userRepository.existsByNickname(kakaoUserInfoDto.nickname())){
                throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
            }

            String fileName;
            String fileUrl;
            if (multipartFile == null || multipartFile.isEmpty()){
                kakaoUser = User.builder()
                        .email(kakaoEmail)
                        .password(passwordEncoder.encode(uuid))
                        .nickname(kakaoUserInfoDto.nickname())
                        .name(kakaoUserInfoDto.name())
                        .birthday(kakaoUserInfoDto.birthday())
                        .userRole(UserRole.USER)
                        .profileImgUrl(kakaoUserInfoDto.profileImgUrl())
                        .kakaoEmail(kakaoEmail)
                        .build();
            } else {
//            fileName = s3Provider.originalFileName(multipartFile);
//            fileUrl = url + serviceRequestDto.nickname() + SEPARATOR + fileName;
                fileUrl = multipartFile.getOriginalFilename();
                kakaoUser = User.builder()
                        .email(kakaoEmail)
                        .password(passwordEncoder.encode(uuid))
                        .nickname(kakaoUserInfoDto.nickname())
                        .name(kakaoUserInfoDto.name())
                        .birthday(kakaoUserInfoDto.birthday())
                        .userRole(UserRole.USER)
                        .profileImgUrl(fileUrl)
                        .kakaoEmail(kakaoEmail)
                        .build();
//            fileUrl = user.getFolderName() + SEPARATOR + fileName;
//            s3Provider.createFolder(serviceRequestDto.email());
//            s3Provider.saveFile(multipartFile, fileUrl);
            }
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    public KakaoUserInfoDto loginWithKakao(String code) throws JsonProcessingException {
        JsonNode jsonNode = getKakaoToken(code);
        String kakaoAccessToken = jsonNode.get("access_token").asText();
        String kakaoRefreshToken = jsonNode.get("refresh_token").asText();
        refreshTimeToLive = jsonNode.get("refresh_token_expires_in").asInt();

        KakaoUserInfoDto kakaoUserInfoDto;
        try{
            kakaoUserInfoDto = getKakaoUserWithAccessToken(kakaoAccessToken);
        } catch (Exception e){
            throw new NotFoundKakaoUserException(UserErrorCode.NOT_FOUND_USER);
        }
        User kakaoUser = userRepository.findByKakaoEmail(kakaoUserInfoDto.kakaoEmail())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        // 서버 토큰 저장
        jwtUtil.addAccessTokenToHeader(kakaoUser, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(kakaoUser, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(kakaoUser.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        httpServletResponse.addHeader("Kakao-Access-Token", kakaoAccessToken);
        RefreshToken kakaoRefresh = new RefreshToken(kakaoUser.getId(), kakaoRefreshToken, refreshTimeToLive.longValue());
        refreshTokenRepository.save(kakaoRefresh);
        return kakaoUserInfoDto;
    }

    public void unlinkKakao(String kakaoAccessToken) throws JacksonException {
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserWithAccessToken(kakaoAccessToken);
        User user = userRepository.findByKakaoEmail(kakaoUserInfo.kakaoEmail())
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
            user.deleteKakaoEmail();
            userRepository.save(user);
        } else {
            log.error("카카오 unlink 실패: {}", response.getBody());
            throw new FailedUnlinkKakaoException(UserErrorCode.FAILED_UNLINK_KAKAO);
        }
    }

    private KakaoUserInfoDto getKakaoUserWithAccessToken(String kakaoAccessToken) throws JacksonException {
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

        String kakaoEmail = jsonNode.get("kakao_account").get("email").asText();
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
        return new KakaoUserInfoDto(kakaoEmail, nickname, name, birth, profileImgUrl);
    }
}
