package com.chatup.chatup_server.service.channels;

import jakarta.validation.constraints.NotNull;

import java.util.Set;


public record ChannelCreateRequest(
        String name,
        @NotNull
        Boolean is_private,
        @NotNull
        Boolean is_direct_message,
        @NotNull
        Set<Long> user_ids
) {}
