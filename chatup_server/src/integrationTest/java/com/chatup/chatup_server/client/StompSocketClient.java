package com.chatup.chatup_server.client;

import com.chatup.chatup_server.service.channels.ChannelInfo;
import com.chatup.chatup_server.service.messaging.IncomingMessage;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import jakarta.websocket.WebSocketContainer;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;


import org.springframework.messaging.simp.stomp.StompSession;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;

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
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule()));
        webSocketStompClient.setMessageConverter(messageConverter);
        this.connectionHandler = connectionHandler;
        webSocketStompClient.start();
    }

    public void connect(String userToken) throws ExecutionException, InterruptedException {
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        StompHeaders stompHeaders = new StompHeaders();
        webSocketHttpHeaders.add("Authorization", format("Bearer %s", userToken));
        webSocketStompClient.connectAsync(this.URL, webSocketHttpHeaders, stompHeaders, this.connectionHandler).get();
        this.session = connectionHandler.getSession();
    }

    @Override
    public void sendMessage(String exchange, String msg) {
        if(this.session!=null){
            this.session.send(exchange, new IncomingMessage(msg));
        }
    }

    @Override
    public List<OutgoingMessage> getMessages() {
        return connectionHandler.getMessages();
    }

    @Override
    public List<ChannelInfo> getEvents() {
        return connectionHandler.getChannelCreationEvents();
    }

    @Override
    public void close(){
        if(this.session!=null){
            this.session.disconnect();
        }
    }
}
