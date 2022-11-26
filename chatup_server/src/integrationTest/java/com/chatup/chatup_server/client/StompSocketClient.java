package com.chatup.chatup_server.client;

import jakarta.websocket.WebSocketContainer;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.converter.StringMessageConverter;


import org.springframework.messaging.simp.stomp.StompSession;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class StompSocketClient implements SocketClient{

    private final WebSocketStompClient webSocketStompClient;
    private final ConnectionHandler connectionHandler;
    private StompSession session;
    private final String URL;

    public StompSocketClient(String url, ConnectionHandler connectionHandler) {
        this.URL = url;
        WebSocketContainer webSocketContainer = new WsWebSocketContainer();
        WebSocketClient webSocketClient = new StandardWebSocketClient(webSocketContainer);
        this.webSocketStompClient = new WebSocketStompClient(webSocketClient);
        webSocketStompClient.setMessageConverter(new StringMessageConverter());
        this.connectionHandler = connectionHandler;
        webSocketStompClient.start();
    }

    public void connect() throws ExecutionException, InterruptedException {
        webSocketStompClient.connectAsync(this.URL, this.connectionHandler).get();
        this.session = connectionHandler.getSession();
    }

    @Override
    public void sendMessage(String topic, String msg) {
        if(this.session!=null){
            this.session.send(topic, msg);
        }
    }

    @Override
    public List<String> getMessages() {
        return connectionHandler.getMessages();
    }

    @Override
    public void close(){
        if(this.session!=null){
            this.session.disconnect();
        }
    }

    @Override
    public void subscribe(String topic){
        connectionHandler.addSubscription(topic);
    }
}
