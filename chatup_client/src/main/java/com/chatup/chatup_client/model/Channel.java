package com.chatup.chatup_client.model;

public record Channel(
        Long channelID,
        String channelName,
        boolean isPrivate,
        boolean isDM
) {
}
