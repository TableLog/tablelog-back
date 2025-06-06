package com.tablelog.tablelogback.domain.board.controller;

import com.tablelog.tablelogback.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public abstract class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    public CustomHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes) throws Exception {

        if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
            HttpServletRequest servletRequest =
                ((org.springframework.http.server.ServletServerHttpRequest) request).getServletRequest();
            // 쿠키에서 accessToken 추출
            String accessToken = jwtUtil.getTokenFromCookie(servletRequest, "accessToken");
            if (accessToken != null) {
                attributes.put("accessToken", accessToken);
            }
        }
        return true;
    }
}
