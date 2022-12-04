package com.chatup.chatup_client.model;

import java.time.Instant;

public record Message(
        Long messageID,
        String content,
        Long authorID,
        String authorUsername,
        Long channelID,
        Instant timeCreated,
        Boolean isDeleted

) {
    @Override
    public String toString() {
        return content;
    }
}
