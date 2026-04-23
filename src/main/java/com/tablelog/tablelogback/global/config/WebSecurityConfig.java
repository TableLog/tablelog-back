package com.tablelog.tablelogback.global.config;

import com.tablelog.tablelogback.global.jwt.JwtAuthenticationFilter;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                )
                .exceptionHandling(ex -> ex
                        // 진짜 인증 실패(토큰 없음/만료)만 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":\"EJ401001\",\"message\":\"토큰이 만료되었습니다.\"}");
                        })
                        // 권한 없음은 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":\"EJ403001\",\"message\":\"접근 권한이 없습니다.\"}");
                        })
                );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsServiceImpl, refreshTokenRepository);
    }

    private CorsConfigurationSource configurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOrigins(List.of(
                    "http://localhost:3000",
                    "http://localhost:8080",
                    "http://localhost:5500",
                    "http://100.82.127.42:8080",
                    "http://100.82.127.42:3000",
                    "https://unity-bottle-registry-defensive.trycloudflare.com",
                    "https://tablelog.n-e.kr",
                    "wss://tablelog.n-e.kr",
                    "https://recommended-couples-conventions-hourly.trycloudflare.com",
                    "http://localhost:5173",
                    "http://localhost:4173",
                    "https://jiangxy.github.io/"
            ));
            config.setMaxAge(3600L);
            config.setAllowCredentials(true);
            config.setExposedHeaders(List.of(
                    "accessToken", "Set-Cookie", "Cookie", "refreshToken",
                    "Kakao-Access-Token", "Kakao-Refresh-Token",
                    "Google-Access-Token", "Google-Refresh-Token"
            ));
            return config;
        };
    }
}