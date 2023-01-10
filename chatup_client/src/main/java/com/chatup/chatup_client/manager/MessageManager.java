package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MessageManager {
    private final Logger logger = LoggerFactory.getLogger(MessageManager.class);
    private final HashMap<Long, MessageBuffer> buffers = new HashMap<>();
    private final boolean testMode;

    public MessageManager(@Value("false") boolean testMode) {
        this.testMode = testMode;
        logger.info("Message manager initialized");
    }

    public MessageBuffer getMessageBuffer(Channel channel) {
        if(buffers.containsKey(channel.getId())) {
            return buffers.get(channel.getId());
        } else {
            MessageBuffer buffer = new MessageBuffer(testMode);
            buffers.put(channel.getId(), buffer);
            return buffer;
        }
    }

    public void addMessage(Message msg) {
        if(buffers.containsKey(msg.getChannelID())) {
            buffers.get(msg.getChannelID()).addMessage(msg);
        } else {
            MessageBuffer buffer = new MessageBuffer(testMode);
            buffer.addMessage(msg);
            buffers.put(msg.getChannelID(), buffer);
        }
    }
    }