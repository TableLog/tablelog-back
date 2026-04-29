package com.tablelog.tablelogback.global.config;

import com.tablelog.tablelogback.global.jwt.JwtAuthenticationFilter;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.exception.JwtErrorCode;
import com.tablelog.tablelogback.global.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

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
                        .requestMatchers("/api/v1/**").permitAll()
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            writeAuthError(response, JwtErrorCode.EXPIRED_JWT_ACCESS_TOKEN.name(), JwtErrorCode.EXPIRED_JWT_ACCESS_TOKEN.getMessage());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // 요구사항: 인증이 필요한 API는 403이 아니라 401로 통일
                            writeAuthError(response, JwtErrorCode.ACCESS_DENIED.name(), JwtErrorCode.ACCESS_DENIED.getMessage());
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
        return new JwtAuthenticationFilter(jwtUtil, userDetailsServiceImpl);
    }

    private void writeAuthError(HttpServletResponse response, String name, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
            "{\"status\":401,\"name\":\"" + name + "\",\"message\":\"" + message + "\"}"
        );
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