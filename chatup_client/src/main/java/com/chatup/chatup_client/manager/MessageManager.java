package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;

import java.util.HashMap;

public class MessageManager {
    private final HashMap<Long, MessageBuffer> buffers = new HashMap<>();
    public MessageBuffer getMessageBuffer(Channel channel) {
        if(buffers.containsKey(channel.channelID())) {
            return buffers.get(channel.channelID());
        } else {
            MessageBuffer buffer = new MessageBuffer();
            buffers.put(channel.channelID(), buffer);
            return buffer;
        }
    }

    public void addMessage(Message msg) {
        if(buffers.containsKey(msg.getChannelID())) {
            buffers.get(msg.getChannelID()).addMessage(msg);
        } else {
            MessageBuffer buffer = new MessageBuffer();
            buffer.addMessage(msg);
            buffers.put(msg.getChannelID(), buffer);
        }
    }
}
