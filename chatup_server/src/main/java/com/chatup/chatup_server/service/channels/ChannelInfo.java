package com.chatup.chatup_server.service.channels;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;

import java.util.ArrayList;
import java.util.List;


public record ChannelInfo(Long id, String name, Boolean isPrivate, Boolean isDirectMessage) {
    public static ChannelInfo from(Long currentUserId, Channel channel){
        String channelName = channel.getName();

        if(channel.getDirectMessage()) {
            List<AppUser> users = new ArrayList<>(channel.getUsers());

            if(users.get(0).getId().equals(currentUserId))
                channelName = users.get(1).getUsername();
            else
                channelName = users.get(0).getUsername();
        }

        return new ChannelInfo(
                channel.getId(),
                channelName,
                channel.getPrivate(),
                channel.getDirectMessage()
        );
    }
}
