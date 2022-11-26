package com.chatup.chatup_client.web;

import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.MessageBuffer;
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
    private final MessageBuffer messageBuffer;
    private final List<String> topics;

    public ConnectionHandler(MessageBuffer messageBuffer, List<String> topics){
        this.messageBuffer = messageBuffer;
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
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String msg = payload.toString();
        logger.info("Got message: {}", msg);
        messageBuffer.addMessage(new Message(msg));
    }
}

