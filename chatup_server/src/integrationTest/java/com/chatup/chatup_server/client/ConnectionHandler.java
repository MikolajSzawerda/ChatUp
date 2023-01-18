package com.chatup.chatup_server.client;

import com.chatup.chatup_server.service.channels.ChannelInfo;
import com.chatup.chatup_server.service.messaging.OutgoingEvent;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
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
    private final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final List<OutgoingMessage> messageBuffer;
    private final String EXCHANGE_ENDPOINT = "/exchange/";

    public List<ChannelInfo> getChannelCreationEvents() {
        return channelCreationBuffer;
    }

    private final List<ChannelInfo> channelCreationBuffer;

    public List<OutgoingMessage> getMessages() {
        return messageBuffer;
    }

    private final String userToken;

    public ConnectionHandler(String userToken){
        this.messageBuffer = new LinkedList<>();
        this.channelCreationBuffer = new LinkedList<>();
        this.userToken = userToken;
    }

    public StompSession getSession() {
        return session;
    }

    private StompSession session;
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        if(this.session != null){
            this.session.subscribe(EXCHANGE_ENDPOINT+this.userToken, this);
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.warn(exception.getMessage());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.warn(exception.getMessage());
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return OutgoingEvent.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        synchronized (this){
            OutgoingEvent event = (OutgoingEvent) payload;
            if(event.eventType().equals("channel_creation")){
                channelCreationBuffer.add((ChannelInfo) event.event());
            } else if(event.eventType().equals("message")) {
                messageBuffer.add((OutgoingMessage)event.event());
            }
        }
    }
}

