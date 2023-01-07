package com.chatup.chatup_client.model;

public record Channel(
        Long id,
        String name,
        boolean isPrivate,
        boolean isDirectMessage
) {
}
