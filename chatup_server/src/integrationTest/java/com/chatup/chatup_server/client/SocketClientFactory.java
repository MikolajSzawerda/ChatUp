package com.chatup.chatup_server.client;

import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.service.AuthService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;

public class SocketClientFactory {
    private final String CHAT_ENDPOINT="ws://localhost:%d/chat";
    private final int port;
    private final AppUserRepository appUserRepository;
    private final AuthService authService;

    public SocketClientFactory(int port, AppUserRepository appUserRepository, AuthService authService) {
        this.port = port;
        this.appUserRepository = appUserRepository;
        this.authService = authService;
    }
    private String getToken(String username){
        var user = appUserRepository.findAppUserByUsername(username);
        return authService.generateTokenForUser(user);
    }

    public SocketClient getClient(String username, List<String> topics){
        ConnectionHandler connectionHandler = new ConnectionHandler(topics);
        StompSocketClient socketClient = new StompSocketClient(format(CHAT_ENDPOINT, port), connectionHandler);
        String userToken = getToken(username);
        try {
            socketClient.connect(userToken);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return socketClient;
    }
}
