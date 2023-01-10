package com.chatup.chatup_client.web;

import com.chatup.chatup_client.controller.ChatViewController;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
public class ConnectionHandler implements StompSessionHandler{
    final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final MessageManager messageManager;
    private final RestClient restClient;
    private final ChannelManager channelManager;
    private String channelCreateTopic;
    private void loadChannelCreateTopic() {
        if(channelCreateTopic == null) {
            channelCreateTopic = "/topic/create." + restClient.getCurrentUser().getUsername();
        }
    }
    private final List<String> topics = new LinkedList<>();
    public void addChannel(Channel channel) {
        String topic = "/topic/channel." + channel.getId();
        topics.add(topic);
        addSubscription(topic);
    }
    @Autowired
    public ConnectionHandler(MessageManager messageManager, RestClient restClient, ChannelManager channelManager) {
        this.messageManager = messageManager;
        this.restClient = restClient;
        this.channelManager = channelManager;
        logger.info("ConnectionHandler created");
    }

    @PostConstruct
    public void init() {
        channelManager.setConnectionHandler(this);
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
        loadChannelCreateTopic();
        this.topics.add(channelCreateTopic);
        addSubscription(channelCreateTopic);
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
        loadChannelCreateTopic();
        if(Objects.equals(headers.getDestination(), channelCreateTopic)){
            return Channel.class;
        }
        return Message.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        loadChannelCreateTopic();
        if(Objects.equals(headers.getDestination(), channelCreateTopic)){
            logger.info("Received channel");
            channelManager.addChannel((Channel) payload);
            return;
        }
        logger.info("Received message");
        synchronized (this) {
            messageManager.addMessage((Message) payload);
        }
    }
}

