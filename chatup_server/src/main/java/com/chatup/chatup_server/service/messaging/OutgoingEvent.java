package com.chatup.chatup_server.service.messaging;


public record OutgoingEvent(
        String eventType,
        Event event
) {
}
