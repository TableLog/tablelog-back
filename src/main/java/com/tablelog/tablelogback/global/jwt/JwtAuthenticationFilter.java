package com.tablelog.tablelogback.global.jwt;

import com.tablelog.tablelogback.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

@Slf4j(topic = "jwt 검증과 인가")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

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

        try {
            Claims info = jwtUtil.getUserInfoFromToken(token); // 여기서 만료/서명/형식 오류를 구분 가능
            setAuthenticationByEmail(info.getSubject());
        } catch (ExpiredJwtException e) {
            writeAuthError(response, "EXPIRED_JWT_ACCESS_TOKEN", "EJ401001");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            writeAuthError(response, "FAILED_JWT_TOKEN", "EJ400001");
            return;
        } catch (Exception e) {
            // JWT 파싱 외 예외는 필터 체인에서 처리(=500로 올라가도록)하여 원인 은폐 방지
            throw new ServletException(e);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationByEmail(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
    }

    private void writeAuthError(HttpServletResponse response, String name, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
            "{\"status\":401,\"name\":\"" + name + "\",\"message\":\"" + message + "\"}"
        );
    }
}
