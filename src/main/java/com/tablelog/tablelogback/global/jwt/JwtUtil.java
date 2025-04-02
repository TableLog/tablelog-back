package com.tablelog.tablelogback.global.jwt;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.jwt.exception.ExpiredJwtAccessTokenException;
import com.tablelog.tablelogback.global.jwt.exception.JwtErrorCode;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_HEADER = "Access-Token";

    @Value("${spring.jwt.access.expiration-period}")
    private Long accessTokenExpirationPeriod;

    @Value("${spring.jwt.refresh.expiration-period}")
    private Long refreshTokenExpirationPeriod;

    @Value("${spring.jwt.secret-key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private SecretKey key;
    public static final Logger logger = LoggerFactory.getLogger("JWT log");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(String email, UserRole role) {
        Date date = new Date();
        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(email)
                        .claim(AUTHORIZATION_KEY, role)
                        .expiration(new Date(date.getTime() + accessTokenExpirationPeriod))
                        .issuedAt(date)
                        .signWith(key, Jwts.SIG.HS256)
                        .compact();
    }

    public boolean validateToken(String token) {
        if(token.contains("Bearer ")){
            token = token.substring(7);
        }
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty");
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        if(token.contains("Bearer ")){
            token = token.substring(7);
        }
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        return null;
    }

    public String addAccessTokenToHeader(final User user, final HttpServletResponse httpServletResponse) {
        String token = createAccessToken(user.getEmail(), user.getUserRole());
        logger.info("header에 AccessToken 추가");
        httpServletResponse.addHeader(ACCESS_TOKEN_HEADER, token);
        return token;
    }

    public Boolean isExpiredAccessToken(String token) {
        if(token.contains("Bearer ")){
            token = token.substring(7);
        }
        try{
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
                    .getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e){
            throw new ExpiredJwtAccessTokenException(JwtErrorCode.EXPIRED_JWT_ACCESS_TOKEN);
        }
    }

    public void expireAccessTokenToHeader(final User user, final HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(ACCESS_TOKEN_HEADER, "");
    }
}
