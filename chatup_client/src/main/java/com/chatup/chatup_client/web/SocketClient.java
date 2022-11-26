package com.chatup.chatup_client.web;

import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.converter.StringMessageConverter;


import org.springframework.messaging.simp.stomp.StompSession;

import javax.websocket.WebSocketContainer;
import java.util.concurrent.ExecutionException;

public class SocketClient {

    private final WebSocketStompClient webSocketStompClient;
    private final ConnectionHandler connectionHandler;
    private StompSession session;
    private final String URL;

    public SocketClient(String url, ConnectionHandler connectionHandler) {
        this.URL = url;
        WebSocketContainer webSocketContainer = new WsWebSocketContainer();
        WebSocketClient webSocketClient = new StandardWebSocketClient(webSocketContainer);
        this.webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new StringMessageConverter());
        this.connectionHandler = connectionHandler;
        webSocketStompClient.start();
    }

    public void connect() throws ExecutionException, InterruptedException {
        webSocketStompClient.connect(this.URL, this.connectionHandler).get();
        this.session = connectionHandler.getSession();
    }

    public void sendMessage(String topic, String message){
        if(this.session!=null){
            this.session.send(topic, message);
        }
    }
}
