package com.chatup.chatup_server.service.channels;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.service.messaging.Event;

import java.util.ArrayList;
import java.util.List;


public record ChannelInfo(Long id, String name, Boolean isPrivate, Boolean isDirectMessage) implements Event {
    public static ChannelInfo from(Long currentUserId, Channel channel){
        String channelName = channel.getName();

        if(channel.getDirectMessage()) {
            List<AppUser> users = new ArrayList<>(channel.getUsers());

            if(users.get(0).getId().equals(currentUserId))
                channelName = users.get(1).getFirstName() + " " + users.get(1).getLastName();
            else
                channelName = users.get(0).getFirstName() + " " + users.get(0).getLastName();
        }

        return new ChannelInfo(
                channel.getId(),
                channelName,
                channel.getPrivate(),
                channel.getDirectMessage()
        );
    }
}
