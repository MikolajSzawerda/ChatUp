package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MessageManager {
    private final Logger logger = LoggerFactory.getLogger(MessageManager.class);
    private final HashMap<Long, MessageBuffer> buffers = new HashMap<>();
    private final boolean testMode;

    public MessageManager(boolean testMode) {
        this.testMode = testMode;
    }

    public MessageBuffer getMessageBuffer(Channel channel) {
        if(buffers.containsKey(channel.channelID())) {
            return buffers.get(channel.channelID());
        } else {
            MessageBuffer buffer = new MessageBuffer(testMode);
            buffers.put(channel.channelID(), buffer);
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
        if(testMode) {
            checkForDuplicates(msg);
            return;
        }
        Platform.runLater(() -> {
            checkForDuplicates(msg);
        });
    }

    public void checkForDuplicates(Message originalMessage) {
        for(MessageBuffer buffer : buffers.values()) {
            for(Message msg : buffer.getMessages()) {
                if(msg.getMessageID().equals(originalMessage.getMessageID()) && !msg.equals(originalMessage)) {
                    logger.warn("Duplicate message found: " + msg + " and " + originalMessage);
                    msg.setDuplicateFlag(true);
                    originalMessage.setDuplicateFlag(true);
                }
                }
        }
        }
    }