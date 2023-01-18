package com.chatup.chatup_client.web;

import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.channels.Channel;
import com.chatup.chatup_client.model.IncomingEvent;
import com.chatup.chatup_client.model.messaging.Message;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;


@Component
public class ConnectionHandler implements StompSessionHandler{
    final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final MessageManager messageManager;
    private final ChannelManager channelManager;
    private String exchangeEndpoint;
    private StompSession session;

    @Autowired
    public ConnectionHandler(MessageManager messageManager, ChannelManager channelManager) {
        this.messageManager = messageManager;
        this.channelManager = channelManager;
        logger.info("ConnectionHandler created");
    }

    public String getExchangeEndpoint() { return exchangeEndpoint; }
    public void setExchangeEndpoint(String exchangeEndpoint) { this.exchangeEndpoint = exchangeEndpoint; }

    public StompSession getSession() { return session; }

    public void addSubscription(String topic){
        if(this.session != null){
            this.session.subscribe(topic, this);
            logger.info("Connected to {}", topic);
        }
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        logger.info("Session established");

        if(exchangeEndpoint != null) {
            addSubscription(exchangeEndpoint);
            logger.info("Subscribed to initial exchange endpoint");
        } else {
            logger.error("Initial exchange endpoint is not set");
        }
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Exception occurred", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.error("Transport error occurred");

    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return IncomingEvent.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        IncomingEvent event = (IncomingEvent) payload;

        if (event.getEventType().equals("message")) {
            Platform.runLater(() -> {
                synchronized (this) {
                    messageManager.addMessage((Message) event.getEvent());
                }
            });
            logger.info("Received message");
        } else if (event.getEventType().equals("channel_creation")) {
            Platform.runLater(() -> {
                synchronized (this) {
                    channelManager.addChannel((Channel) event.getEvent());
                }
            });
            logger.info("Received channel");
        } else {
            logger.error("Received unknown event");
        }
    }

}

