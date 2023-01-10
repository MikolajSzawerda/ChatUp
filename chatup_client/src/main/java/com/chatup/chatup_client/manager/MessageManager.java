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
        boolean added = false;
        if(buffers.containsKey(msg.getChannelID())) {
            added = buffers.get(msg.getChannelID()).addMessage(msg);
        } else {
            MessageBuffer buffer = new MessageBuffer(testMode);
            added = buffer.addMessage(msg);
            buffers.put(msg.getChannelID(), buffer);
        }
        if(!added) return;
        if(testMode) {
            checkForDuplicates(msg);
            return;
        }
        Platform.runLater(() -> checkForDuplicates(msg));
    }

    public void checkForDuplicates(Message originalMessage) {
        for(MessageBuffer buffer : buffers.values()) {
            for(Message msg : buffer.getMessages()) {
                if(msg.getMessageID().equals(originalMessage.getMessageID()) && msg != originalMessage ) { // comparing using != intentional
                    logger.warn("Duplicate message found: " + msg + " and " + originalMessage);
                    msg.setDuplicateFlag(true);
                    originalMessage.setDuplicateFlag(true);
                }
                }
        }
        }
    }