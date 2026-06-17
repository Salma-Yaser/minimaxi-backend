package com.minimaxi.backend.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String query = request.getURI().getQuery(); // userId=1&token=xxx
        if (query == null) return false;

        String token = null;
        String userIdStr = null;

        for (String param : query.split("&")) {
            if (param.startsWith("token=")) token = param.substring(6);
            if (param.startsWith("userId=")) userIdStr = param.substring(7);
        }

        if (token == null || userIdStr == null) return false;
        if (!jwtUtil.isTokenValid(token)) return false;

        Long userIdFromToken = jwtUtil.extractUserId(token);
        Long userIdFromParam = Long.parseLong(userIdStr);

        if (!userIdFromToken.equals(userIdFromParam)) return false;

        attributes.put("userId", userIdFromParam);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}