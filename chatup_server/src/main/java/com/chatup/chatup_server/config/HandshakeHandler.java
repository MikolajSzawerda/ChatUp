package com.chatup.chatup_server.config;

import com.chatup.chatup_server.service.JwtTokenService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class HandshakeHandler extends DefaultHandshakeHandler {
    private static final String ATTR_PRINCIPAL = "__principal__";
    private static final String ATTR_USERNAME = "__username__";
    private static final String AUTHORIZATION = "authorization";
    private final JwtTokenService jwtTokenService;

    public HandshakeHandler(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    private String extractToken(ServerHttpRequest request){
        return request.getHeaders().get(AUTHORIZATION).get(0).split(" ")[1];
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request);
        String username = jwtTokenService.getUsernameFromToken(token);
        attributes.put(ATTR_PRINCIPAL, token);
        attributes.put(ATTR_USERNAME, username);
        return () -> username;
    }
}
