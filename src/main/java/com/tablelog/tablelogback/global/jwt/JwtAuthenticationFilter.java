package com.tablelog.tablelogback.global.jwt;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.jwt.exception.ExpiredJwtRefreshTokenException;
import com.tablelog.tablelogback.global.jwt.exception.FailedJwtTokenException;
import com.tablelog.tablelogback.global.jwt.exception.JwtErrorCode;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import com.tablelog.tablelogback.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

@Slf4j(topic = "jwt 검증과 인가")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull FilterChain filterChain)
            throws IOException, ServletException {
        String token = jwtUtil.getTokenFromCookie(request, "accessToken");

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 변경 부분
        if (jwtUtil.validateToken(token)) {
            setAuthentication(token);
        }
        else {
            String refresh = jwtUtil.getTokenFromCookie(request, "refreshToken");
            Optional<RefreshToken> optional = refreshTokenRepository.findByRefreshToken(refresh);
            if (optional.isEmpty()) {
                jwtUtil.deleteCookie("accessToken", response);
                jwtUtil.deleteCookie("refreshToken", response);
                throw new ExpiredJwtRefreshTokenException(JwtErrorCode.EXPIRED_JWT_REFRESH_TOKEN);
            }
            RefreshToken refreshToken = optional.get();
            if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
                jwtUtil.deleteCookie("accessToken", response);
                jwtUtil.deleteCookie("refreshToken", response);
                throw new FailedJwtTokenException(JwtErrorCode.FAILED_JWT_TOKEN);
            }

            // accessToken 갱신
            String email = jwtUtil.getEmailFromToken(refresh);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(email);
            User user = userDetails.getUser();
            jwtUtil.deleteCookie("accessToken", response);
            String newAccessToken = jwtUtil.addTokenToCookie(user, response, "accessToken");
            log.info("{}의 accessToken 갱신", user.getEmail());
            setAuthentication(newAccessToken);
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication (String token) {
        Claims info = jwtUtil.getUserInfoFromToken(token);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(info.getSubject());
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
    }
}
