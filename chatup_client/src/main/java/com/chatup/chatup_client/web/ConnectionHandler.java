package com.chatup.chatup_client.web;

import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.manager.MessageBuffer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ConnectionHandler implements StompSessionHandler{
    Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final MessageManager messageManager;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> topics;

    public ConnectionHandler(MessageManager messageManager, List<String> topics){
        this.messageManager = messageManager;
        this.topics = new LinkedList<>(topics);
    }

    public void addSubscription(String topic){
        topics.add(topic);
        if(this.session != null){
            this.session.subscribe(topic, this);
            logger.info("Connected to {}", topic);
        }
    }

    public StompSession getSession() {
        return session;
    }

    private StompSession session;
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        logger.info("Session established");
        this.topics.forEach(this::addSubscription);
        logger.info("Initial subscriptions");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Message.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info("Received message");
        synchronized (this) {
            messageManager.addMessage((Message) payload);
        }
    }
}

