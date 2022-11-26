package com.chatup.chatup_server.client;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;

public class SocketClientFactory {
    private final String CHAT_ENDPOINT="ws://localhost:%d/chat";
    private final int port;

    public SocketClientFactory(int port) {
        this.port = port;
    }


    public SocketClient getClient(List<String> topics){
        ConnectionHandler connectionHandler = new ConnectionHandler(topics);
        StompSocketClient socketClient = new StompSocketClient(format(CHAT_ENDPOINT, port), connectionHandler);
        try {
            socketClient.connect();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return socketClient;
    }
}
