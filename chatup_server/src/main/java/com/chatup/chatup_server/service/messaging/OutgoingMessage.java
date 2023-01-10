package com.chatup.chatup_server.service.messaging;

import com.chatup.chatup_server.domain.Message;

import java.time.Instant;

public record OutgoingMessage(
        Long messageID,
        String content,
        Long authorID,
        String authorUsername,
        String authorFirstName,
        String authorLastName,
        Long channelID,
        Instant timeCreated,
        Boolean isDeleted
) implements Event{
    public static OutgoingMessage from(Message message){
        return new OutgoingMessage(
                message.getID(),
                message.getContent(),
                message.getAuthor().getId(),
                message.getAuthor().getUsername(),
                message.getAuthor().getFirstName(),
                message.getAuthor().getLastName(),
                message.getChannel().getId(),
                message.getTimeCreated(),
                message.getDeleted());
    }
}
