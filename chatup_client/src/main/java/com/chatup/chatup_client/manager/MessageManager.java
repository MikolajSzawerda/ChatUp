package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.RestClient;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MessageManager {
    private final Logger logger = LoggerFactory.getLogger(MessageManager.class);
    private final HashMap<Long, MessageBuffer> buffers = new HashMap<>();
    private final boolean testMode;
    private final RestClient restClient;

    @Autowired
    public MessageManager(@Value("false") boolean testMode, RestClient restClient) {
        this.testMode = testMode;
        this.restClient = restClient;
        logger.info("Message manager initialized");
    }

    public MessageBuffer getMessageBuffer(Long channelID) {
        if(buffers.containsKey(channelID)) {
            return buffers.get(channelID);
        } else {
            MessageBuffer buffer = new MessageBuffer(testMode, restClient, channelID, logger);
            buffers.put(channelID, buffer);
            return buffer;
        }
    }

    public void addMessage(Message msg) {
        if(buffers.containsKey(msg.getChannelID())) {
            buffers.get(msg.getChannelID()).addMessage(msg);
        } else {
            MessageBuffer buffer = new MessageBuffer(testMode, restClient, msg.getChannelID(), logger);
            buffer.addMessage(msg);
            buffers.put(msg.getChannelID(), buffer);
        }
    }
    }