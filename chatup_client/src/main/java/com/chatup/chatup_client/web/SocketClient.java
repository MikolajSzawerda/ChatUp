package com.chatup.chatup_client.web;

import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.converter.StringMessageConverter;


import org.springframework.messaging.simp.stomp.StompSession;

import javax.websocket.WebSocketContainer;
import java.util.concurrent.ExecutionException;

public class SocketClient {
    private final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private final String URL;
    private final String token;
    private final ConnectionHandler connectionHandler;
    private StompSession session;
    private final WebSocketStompClient webSocketStompClient;

    public SocketClient(String url, String token, ConnectionHandler connectionHandler) {
        this.URL = url;
        this.token = token;
        this.connectionHandler = connectionHandler;

        WebSocketContainer webSocketContainer = new WsWebSocketContainer();
        WebSocketClient webSocketClient = new StandardWebSocketClient(webSocketContainer);
        this.webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new StringMessageConverter());
        webSocketStompClient.start();
    }

    public void connect() throws ExecutionException, InterruptedException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        StompHeaders stompHeaders = new StompHeaders();
        headers.add("Authorization", "Bearer " + this.token);
        webSocketStompClient.connect(this.URL, headers, stompHeaders, this.connectionHandler).get();
        this.session = connectionHandler.getSession();
    }

    public void sendMessage(String topic, String message){
        if(this.session!=null){
            logger.info("Sending message {} to {}", message, topic);
            this.session.send(topic, message);
        }
    }
}
