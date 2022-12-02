package com.chatup.chatup_server.client;

import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ConnectionHandler implements StompSessionHandler{
    private final List<OutgoingMessage> messageBuffer;

    public List<OutgoingMessage> getMessages() {
        return messageBuffer;
    }

    private final List<String> topics;

    public ConnectionHandler(List<String> topics){
        this.messageBuffer = new LinkedList<>();
        this.topics = new LinkedList<>(topics);
    }

    public void addSubscription(String topic){
        topics.add(topic);
        if(this.session != null){
            this.session.subscribe(topic, this);
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public StompSession getSession() {
        return session;
    }

    private StompSession session;
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        this.topics.forEach(this::addSubscription);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return OutgoingMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        synchronized (this){
            messageBuffer.add((OutgoingMessage)payload);
        }
    }
}

