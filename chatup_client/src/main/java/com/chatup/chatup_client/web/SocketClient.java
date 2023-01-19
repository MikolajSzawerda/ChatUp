package com.chatup.chatup_client.web;

import com.chatup.chatup_client.config.AppConfig;
import com.chatup.chatup_client.model.messaging.OutgoingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.simp.stomp.StompSession;

import javax.websocket.WebSocketContainer;
import java.util.concurrent.ExecutionException;

@Component
public class SocketClient {
    private final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private final String URL;
    private final ConnectionHandler connectionHandler;
    private final AppConfig appConfig;
    private final AuthClient authClient;
    private StompSession session;
    private final WebSocketStompClient webSocketStompClient;

    public SocketClient(AppConfig appConfig, AuthClient authClient, ConnectionHandler connectionHandler) {
        this.authClient = authClient;
        this.appConfig = appConfig;
        this.connectionHandler = connectionHandler;
        this.URL = appConfig.getWebSocketURL();
        logger.info("SocketClient created");

        WebSocketContainer webSocketContainer = new WsWebSocketContainer();
        WebSocketClient webSocketClient = new StandardWebSocketClient(webSocketContainer);
        this.webSocketStompClient = new WebSocketStompClient(webSocketClient);
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule()));
        webSocketStompClient.setMessageConverter(messageConverter);
        webSocketStompClient.start();
    }

    public void connect() throws ExecutionException, InterruptedException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        StompHeaders stompHeaders = new StompHeaders();
        headers.add("Authorization", "Bearer " + authClient.getToken());

        connectionHandler.setExchangeEndpoint("/exchange/" + authClient.getToken());
        webSocketStompClient.connect(this.URL, headers, stompHeaders, this.connectionHandler).get();

        this.session = connectionHandler.getSession();
    }

    public void sendMessage(String topic, String message){
        if(this.session != null){
            logger.info("Sending message {} to {}", message, topic);
            this.session.send(topic, new OutgoingMessage(message));
        }
    }

    public void close() {
        if(this.session != null)
            this.session.disconnect();
    }

    public boolean isConnected() {
        return this.session != null;
    }
}
