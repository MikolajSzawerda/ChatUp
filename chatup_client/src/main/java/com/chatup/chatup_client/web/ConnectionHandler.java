package com.chatup.chatup_client.web;

import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Component
public class ConnectionHandler implements StompSessionHandler{
    final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final MessageManager messageManager;

    // TODO: remove once channel manager is working
    private final List<String> topics = new LinkedList<>(){{
        add("/topic/channel/1");
    }};

    @Autowired
    public ConnectionHandler(MessageManager messageManager) {
        this.messageManager = messageManager;
        logger.info("ConnectionHandler created");
    }

    public void addSubscription(String topic){
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
        logger.error("Exception occurred", exception);
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

