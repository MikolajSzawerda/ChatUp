package com.chatup.chatup_client.model.channels;

import com.chatup.chatup_client.model.channels.Channel;
import javafx.beans.binding.ObjectBinding;

public class ChannelBinding extends ObjectBinding{
    private Channel channel;

    public ChannelBinding(Channel channel){
        this.channel = channel;
    }
    @Override
    protected Object computeValue() {
        return channel.getId() == null ? null: channel.getId();
    }
}
