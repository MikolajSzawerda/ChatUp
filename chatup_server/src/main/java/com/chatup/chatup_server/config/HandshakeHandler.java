package com.chatup.chatup_server.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class HandshakeHandler extends DefaultHandshakeHandler {
    private static final String ATTR_PRINCIPAL = "__principal__";
    private static final String AUTHORIZATION = "authorization";

    private String extractToken(ServerHttpRequest request){
        return request.getHeaders().get(AUTHORIZATION).get(0).split(" ")[1];
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request);
        attributes.put(ATTR_PRINCIPAL, token);
        return () -> token;
    }
}
