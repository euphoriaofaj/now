package com.morago.backend.config.websocket;

import com.morago.backend.config.utils.JWTUtils;
import com.morago.backend.entity.enumFiles.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JWTUtils jwtUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                jwtUtils.validateToken(token, TokenType.ACCESS);
                String username = jwtUtils.getUsernameFromToken(token, TokenType.ACCESS);
                attributes.put("username", username);
                log.debug("WebSocket handshake successful for user: {}", username);
                return true;
            } catch (Exception e) {
                log.warn("WebSocket handshake failed: invalid token");
            }
        }
        
        log.warn("WebSocket handshake failed: no valid token");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Nothing to do after handshake
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        // Try to get token from query parameter
        String token = request.getURI().getQuery();
        if (token != null && token.startsWith("token=")) {
            return token.substring(6);
        }
        
        // Try to get token from Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }
}